package com.extole.client.rest.impl.events;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.events.ConversionEndpoints;
import com.extole.client.rest.events.ConversionResponse;
import com.extole.client.rest.impl.events.ConversionRequest.ConversionException;
import com.extole.client.rest.impl.person.provider.legacy.LegacyClientEventPersonLookupOrCreateResult;
import com.extole.client.rest.impl.person.provider.legacy.LegacyClientEventPersonLookupService;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.exception.EventProcessorClientHasNoDomainException;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.zone.targeter.service.ZoneTargeterBuilder;
import com.extole.zone.targeter.service.ZoneTargeterService;

@Deprecated // TODO to be removed in ENG-12938
@Provider
public class ConversionEndpointsImpl implements ConversionEndpoints {

    private static final String CONVERSION_EVENT_NAME = "conversion";
    private static final String ZONE_PARAMETER_EMAIL = "email";

    private final HttpServletRequest servletRequest;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ZoneTargeterService zoneTargeterService;
    private final LegacyClientEventPersonLookupService eventPersonLookupService;
    private final ClientRequestContextService clientRequestContextService;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    public ConversionEndpointsImpl(@Context HttpServletRequest servletRequest,
        ClientAuthorizationProvider authorizationProvider,
        LegacyClientEventPersonLookupService eventPersonLookupService,
        ZoneTargeterService zoneTargeterService,
        ClientRequestContextService clientRequestContextService,
        ExtoleMetricRegistry metricRegistry) {
        this.servletRequest = servletRequest;
        this.authorizationProvider = authorizationProvider;
        this.eventPersonLookupService = eventPersonLookupService;
        this.zoneTargeterService = zoneTargeterService;
        this.clientRequestContextService = clientRequestContextService;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Response v3Convert(String accessToken, HttpServletRequest httpRequest)
        throws UserAuthorizationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ConversionRequest conversion =
            new ConversionRequest(Long.valueOf(authorization.getClientId().getValue()), httpRequest);
        try {
            String actionId = convert(authorization, conversion).getValue();
            return Response.ok().entity(new ConversionResponse(
                "success", "Incentive status was NONE;", actionId))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (AuthorizationException e) {
            return Response.status(Status.BAD_REQUEST)
                .entity(conversion.new ConversionException("Authentication Failed"))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (EventProcessorClientHasNoDomainException e) {
            return Response.ok().entity(new ConversionResponse(
                "Unable to find a valid program domain for conversion. Request: " + conversion))
                .header("Cache-Control", "no-cache")
                .build();
        } catch (ConversionException | EventProcessorException e) {
            return Response.ok().entity(new ConversionResponse(e.getMessage()))
                .header("Cache-Control", "no-cache")
                .build();
        }
    }

    private Id<? extends ConsumerEvent> convert(ClientAuthorization authorization, ConversionRequest conversion)
        throws ConversionException, AuthorizationException, EventProcessorException {

        conversion.validate();
        conversion.checkDateRange();

        if (isNullOrEmpty(conversion.getPartnerConversionId())) {
            throw conversion.new ConversionException(
                "No fields from {click_id, coupon_code, advocate_code, partner_conversion_id, partner_user_id}"
                    + " were set. Request: " + conversion);
        }

        Map<String, String> parameters = new HashMap<>(conversion.getMergedParams());
        List<String> notes = new ArrayList<>();

        LegacyClientEventPersonLookupOrCreateResult lookupResult;
        try {
            lookupResult = eventPersonLookupService.newLookup(authorization)
                .withPartnerConversionId(conversion.getPartnerConversionId())
                .withEmail(conversion.getEmail())
                .lookupOrCreate();
        } catch (PersonNotFoundException e) {
            throw conversion.new ConversionException("Unable to find person with personId=" + e.getPersonId()
                + " defined in request: " + conversion);
        }

        Person person = lookupResult.getPerson();

        notes.addAll(lookupResult.getLogMessages());

        if (!notes.isEmpty()) {
            parameters.put("extole_note", StringUtils.join(notes, ";\n"));
        }

        parameters.put("partner_conversion_id", conversion.getPartnerConversionId());
        parameters.put(ZONE_PARAMETER_EMAIL, conversion.getEmail());

        ClientRequestContext requestContext = clientRequestContextService
            .createBuilder(authorization, servletRequest)
            .withEventName(CONVERSION_EVENT_NAME)
            .withEventProcessing(configurator -> {
                parameters.forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, true, true));
                });
            })
            .build();

        ExecutionLogger logger = ExecutionLoggerFactory.newInstance(getClass());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            ZoneTargeterBuilder zoneTargeterBuilder = zoneTargeterService.createBuilder(
                requestContext.getAuthorization(), person, requestContext.getProcessedRawEvent(), logger);
            if (conversion.getActionDate() != null) {
                zoneTargeterBuilder.withEventTime(Instant.ofEpochMilli(conversion.getActionDate().longValue()));
            }
            return zoneTargeterBuilder.targetAndSendConsumerEvents().getInputEvent().getId();
        } finally {
            stopwatch.stop();
            EventDispatcherMetrics.EVENT_CONVERSION_DURATION.updateDuration(metricRegistry,
                stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

}
