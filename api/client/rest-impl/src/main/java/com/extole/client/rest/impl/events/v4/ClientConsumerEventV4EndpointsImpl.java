package com.extole.client.rest.impl.events.v4;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.events.v4.ClientConsumerEventV4Endpoints;
import com.extole.client.rest.events.v4.ClientConsumerEventV4Request;
import com.extole.client.rest.events.v4.ClientConsumerEventV4Response;
import com.extole.client.rest.events.v4.ClientConsumerEventV4RestException;
import com.extole.client.rest.impl.person.provider.legacy.LegacyClientEventPersonProviderFactory;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.log.execution.ExecutionLoggerFactory;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.exception.EventProcessorPersonNotFoundException;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.zone.targeter.service.ZoneTargeterBuilder;
import com.extole.zone.targeter.service.ZoneTargeterService;

@Deprecated // TODO REMOVE IN ENG-10566
@Provider
public class ClientConsumerEventV4EndpointsImpl implements ClientConsumerEventV4Endpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ClientConsumerEventV4EndpointsImpl.class);

    private final HttpServletRequest servletRequest;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ProgramDomainCache programCache;
    private final ClientRequestContextService clientRequestContextService;
    private final LegacyClientEventPersonProviderFactory personProviderFactory;
    private final ZoneTargeterService zoneTargeterService;

    @Inject
    public ClientConsumerEventV4EndpointsImpl(@Context HttpServletRequest servletRequest,
        ClientAuthorizationProvider authorizationProvider,
        ProgramDomainCache programCache,
        ClientRequestContextService clientRequestContextService,
        LegacyClientEventPersonProviderFactory personProviderFactory,
        ZoneTargeterService zoneTargeterService) {
        this.servletRequest = servletRequest;
        this.authorizationProvider = authorizationProvider;
        this.programCache = programCache;
        this.clientRequestContextService = clientRequestContextService;
        this.personProviderFactory = personProviderFactory;
        this.zoneTargeterService = zoneTargeterService;
    }

    @Override
    public ClientConsumerEventV4Response submit(String accessToken, ClientConsumerEventV4Request request,
        ZoneId timeZone) throws UserAuthorizationRestException, ClientConsumerEventV4RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        Id<ClientHandle> clientId = authorization.getClientId();
        LOG.warn("ENG-10566 v4 events request coming in for client={} performer={} event_name={}", clientId,
            authorization.getIdentityId(), request.getEventName());

        if (Strings.isNullOrEmpty(request.getEventName())) {
            throw RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
                .withErrorCode(ClientConsumerEventV4RestException.MISSING_EVENT_NAME)
                .addParameter("client_id", clientId.getValue())
                .build();
        }

        Map<String, String> data = getDataWithoutExtolePrefix(request);
        String personId = !Strings.isNullOrEmpty(request.getPersonId()) ? request.getPersonId()
            : Strings.emptyToNull(data.get(ClientConsumerEventV4Request.DATA_PERSON_ID));

        data.put(ClientConsumerEventV4Request.DATA_PERSON_ID, personId);

        String viaClickId = Strings.emptyToNull(data.get(ClientConsumerEventV4Request.DATA_CLICK_ID));
        if (viaClickId != null) {
            LOG.warn("ENG-8070 received via_click_id parameter for client: {}, request: {}", clientId, request);
        }

        Instant eventTime = request.getEventTime().isPresent() ? request.getEventTime().get().toInstant()
            : parseEventTime(clientId, timeZone,
                Strings.emptyToNull(data.get(ClientConsumerEventV4Request.DATA_EVENT_DATE)));

        PublicProgram programDomain = getClientDomain(clientId, data);
        try {
            ClientRequestContext requestContext = clientRequestContextService
                .createBuilder(authorization, servletRequest)
                .withEventName(request.getEventName())
                .withEventProcessing(configurator -> {
                    configurator.withClientDomain(programDomain);
                    data.forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                    });
                    configurator.addPrehandler(personProviderFactory.newPersonPrehandler(authorization, false));
                })
                .withCandidateProvider(personProviderFactory.newPersonProvider(authorization, false))
                .build();

            Id<? extends ConsumerEvent> inputConsumerEventId = sendInputEvent(requestContext, eventTime);
            return new ClientConsumerEventV4Response(inputConsumerEventId.getValue(),
                inputConsumerEventId.getValue(),
                inputConsumerEventId.getValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventProcessorPersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
                .withErrorCode(ClientConsumerEventV4RestException.PERSON_NOT_FOUND)
                .addParameter("client_id", e.getClientId().getValue())
                .addParameter("person_id", e.getPersonId().getValue())
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private Id<? extends ConsumerEvent> sendInputEvent(ClientRequestContext requestContext, @Nullable Instant eventTime)
        throws AuthorizationException {
        ZoneTargeterBuilder zoneTargeterBuilder =
            zoneTargeterService.createBuilder(requestContext.getAuthorization(), requestContext.getPerson().get(),
                requestContext.getProcessedRawEvent(), ExecutionLoggerFactory.newInstance());
        if (eventTime != null) {
            zoneTargeterBuilder.withEventTime(eventTime);
        }
        return zoneTargeterBuilder.targetAndSendConsumerEvents().getInputEvent().getId();
    }

    private PublicProgram getClientDomain(Id<ClientHandle> clientId, Map<String, String> data)
        throws ClientConsumerEventV4RestException {
        String clientDomainName = Strings.emptyToNull(data.get(ClientConsumerEventV4Request.DATA_CLIENT_DOMAIN_NAME));
        String siteHostName = Strings.emptyToNull(data.get(ClientConsumerEventV4Request.DATA_SITE_HOST_NAME));
        if (clientDomainName != null) {
            return getClientDomainByName(clientId, clientDomainName);
        }
        if (siteHostName != null) {
            return getClientDomainBySiteHostName(clientId, siteHostName);
        }
        return getRandomClientDomain(clientId);
    }

    @Nullable
    private Instant parseEventTime(Id<ClientHandle> clientId, ZoneId timeZone, @Nullable String eventTime)
        throws ClientConsumerEventV4RestException {
        try {
            return eventTime == null ? null
                : new DateTimeBuilder().withDateString(eventTime).withDefaultTimezone(timeZone).build().toInstant();
        } catch (DateTimeParseException | DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
                .withErrorCode(ClientConsumerEventV4RestException.INVALID_EVENT_DATE_FORMAT)
                .addParameter("client_id", clientId.getValue())
                .addParameter("event_date", eventTime)
                .withCause(e).build();
        }
    }

    private PublicProgram getRandomClientDomain(Id<ClientHandle> clientId)
        throws ClientConsumerEventV4RestException {
        try {
            return programCache.getDefaultProgram(clientId)
                .orElseThrow(() -> new ProgramNotFoundException("No program domain found for client_id: " + clientId));
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
                .withErrorCode(ClientConsumerEventV4RestException.CLIENT_HAS_NO_DOMAIN)
                .addParameter("client_id", clientId.getValue())
                .withCause(e).build();
        }
    }

    private PublicProgram getClientDomainByName(Id<ClientHandle> clientId, String programDomain)
        throws ClientConsumerEventV4RestException {
        if (!InternetDomainName.isValid(programDomain)) {
            throw getInvalidClientConsumerDomainExceptionBuilder(clientId, programDomain).build();
        }
        try {
            PublicProgram matchingProgram = programCache.getByClientId(clientId).stream()
                .filter(program -> program.getProgramDomain().equals(InternetDomainName.from(programDomain)))
                .findFirst()
                .orElseThrow(() -> new ProgramNotFoundException("Cannot find program domain for client_id: "
                    + clientId + " with domain: " + programDomain));
            return programCache.getForwardedById(matchingProgram.getId(), clientId);
        } catch (ProgramNotFoundException e) {
            throw getInvalidClientConsumerDomainExceptionBuilder(clientId, programDomain).withCause(e).build();
        }
    }

    private RestExceptionBuilder<ClientConsumerEventV4RestException> getInvalidClientConsumerDomainExceptionBuilder(
        Id<ClientHandle> clientId, String programDomain) {
        return RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
            .addParameter("client_id", clientId.getValue())
            .addParameter("client_domain_name", programDomain)
            .withErrorCode(ClientConsumerEventV4RestException.INVALID_CLIENT_DOMAIN);
    }

    private PublicProgram getClientDomainBySiteHostName(Id<ClientHandle> clientId, String siteHostName)
        throws ClientConsumerEventV4RestException {
        try {
            return programCache.getByClientIdAndSiteHostName(clientId, siteHostName);
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientConsumerEventV4RestException.class)
                .withErrorCode(ClientConsumerEventV4RestException.INVALID_SITE)
                .addParameter("client_id", clientId.getValue())
                .addParameter("site_host_name", siteHostName)
                .withCause(e).build();
        }
    }

    private Map<String, String> getDataWithoutExtolePrefix(ClientConsumerEventV4Request request) {
        Map<String, String> cleanData = new HashMap<>();
        Map<String, String> data = request.getData();
        data.keySet().stream()
            .forEach(key -> cleanData.put(key.replaceFirst("^extole_", ""), Optional.ofNullable(data.get(key))
                .map(value -> "undefined".equals(value) || "null".equals(value) ? null : value).orElse(null)));
        return cleanData;
    }
}
