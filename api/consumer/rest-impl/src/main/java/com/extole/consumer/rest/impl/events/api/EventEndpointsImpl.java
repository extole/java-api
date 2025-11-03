package com.extole.consumer.rest.impl.events.api;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.log.execution.ExecutionLogger;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.events.api.EventEndpoints;
import com.extole.consumer.rest.events.api.SubmitEventRequest;
import com.extole.consumer.rest.events.api.SubmitEventResponse;
import com.extole.consumer.rest.events.api.SubmitEventRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.consumer.service.zone.ZoneResponseType;
import com.extole.event.consumer.raw.RequestLogLevel;
import com.extole.event.consumer.step.StepConsumerEvent;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.zone.targeter.service.ZoneTargeterEventResult;
import com.extole.zone.targeter.service.ZoneTargeterService;

@Provider
public class EventEndpointsImpl implements EventEndpoints {

    private static final Pattern EVENT_PATTERN = Pattern.compile("^[^\\s][0-9A-Za-z\\s_.-]+[^\\s]");

    private final ConsumerRequestContextService consumerRequestContextService;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final ZoneTargeterService zoneTargeterService;
    private final ExtoleMetricRegistry metricRegistry;
    private final ClientCache clientCache;

    @Inject
    public EventEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        HttpServletRequest servletRequest,
        HttpServletResponse servletResponse,
        ZoneTargeterService zoneTargeterService,
        ExtoleMetricRegistry metricRegistry,
        ClientCache clientCache) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.consumerRequestContextService = consumerRequestContextService;
        this.zoneTargeterService = zoneTargeterService;
        this.metricRegistry = metricRegistry;
        this.clientCache = clientCache;
    }

    @Override
    public SubmitEventResponse submit(String accessToken, SubmitEventRequest request)
        throws AuthorizationRestException, SubmitEventRestException {
        if (Strings.isNullOrEmpty(request.getEventName())) {
            throw RestExceptionBuilder.newBuilder(SubmitEventRestException.class)
                .withErrorCode(SubmitEventRestException.MISSING_EVENT_NAME)
                .build();
        }
        if (!EVENT_PATTERN.matcher(request.getEventName()).matches()) {
            throw RestExceptionBuilder.newBuilder(SubmitEventRestException.class)
                .withErrorCode(SubmitEventRestException.INVALID_EVENT_NAME)
                .build();
        }

        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(request.getEventName())
            .withEventProcessing(configurator -> {
                request.getData().forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
                request.getJwt().ifPresent(jwt -> configurator.addJwt(jwt, EventData.Source.REQUEST_BODY));
                request.getIdToken()
                    .ifPresent(idToken -> configurator.addIdToken(idToken, EventData.Source.REQUEST_BODY));
            })
            .build();

        PersonAuthorization authorization = requestContext.getAuthorization();
        ExecutionLogger logger = ExecutionLoggerFactory.newInstance(getClass());

        String clientShortName;
        try {
            clientShortName = clientCache.getById(authorization.getClientId()).getShortName();
        } catch (ClientNotFoundException e) {
            // should not happen
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        ZoneTargeterEventResult zoneTargeterEventResult = null;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            zoneTargeterEventResult = zoneTargeterService.createBuilder(authorization, authorization.getIdentity(),
                requestContext.getProcessedRawEvent(), logger)
                .targetAndSendConsumerEvents();
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } finally {
            stopwatch.stop();

            Optional<StepConsumerEvent> stepEvent =
                zoneTargeterEventResult != null ? zoneTargeterEventResult.getStepEvent() : Optional.empty();
            ZoneMetrics.EVENT_API_DURATION.updateDurationForClient(metricRegistry,
                stopwatch.elapsed(TimeUnit.MILLISECONDS), clientShortName, ZoneResponseType.EVENT, stepEvent);
        }

        RequestLogLevel logLevel = requestContext.getProcessedRawEvent().getRawEvent().getRequestLogLevel();
        if (logLevel != RequestLogLevel.NONE) {
            logger.getLogMessages()
                .forEach(message -> servletResponse.addHeader(ZoneRenderResponse.HEADER_EXTOLE_LOG, message));
        }
        if (logLevel == RequestLogLevel.TRACE) {
            requestContext.getPerformanceLogMessages()
                .forEach(message -> servletResponse.addHeader(ZoneRenderResponse.HEADER_EXTOLE_LOG, message));
            logger.getMessages(ExecutionLogger.Level.TRACE)
                .forEach(message -> servletResponse.addHeader(ZoneRenderResponse.HEADER_EXTOLE_LOG, message));
        }

        return new SubmitEventResponse(zoneTargeterEventResult.getInputEvent().getId().getValue());
    }

}
