package com.extole.client.zone.rest.impl;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientApiType;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.consumer.event.service.input.person.provider.ClientEventPersonProviderFactory;
import com.extole.client.zone.rest.ClientRenderZoneRequest;
import com.extole.client.zone.rest.ClientZoneRestException;
import com.extole.client.zone.rest.ClientZonesEndpoints;
import com.extole.client.zone.rest.UnnamedClientRenderZoneRequest;
import com.extole.client.zone.rest.impl.zone.rendering.ClientZoneRenderer;
import com.extole.client.zone.rest.impl.zone.rendering.PersonLockAcquireException;
import com.extole.client.zone.rest.impl.zone.rendering.ZoneInvalidRedirectException;
import com.extole.client.zone.rest.impl.zone.rendering.response.ClientZoneRenderResponse;
import com.extole.common.lang.JsonMap;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.service.zone.ZoneEventResult;
import com.extole.consumer.service.zone.ZoneMetrics;
import com.extole.consumer.service.zone.ZoneRenderResponse;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.person.service.profile.PersonLockAcquireRuntimeException;

@Provider
public class ClientZonesEndpointsImpl implements ClientZonesEndpoints {

    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final ClientCache clientCache;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientRequestContextService clientRequestContextService;
    private final ClientZoneRenderer clientZoneRenderer;
    private final ClientEventPersonProviderFactory personProviderFactory;

    @Inject
    public ClientZonesEndpointsImpl(
        @Context HttpServletRequest servletRequest,
        @Context HttpServletResponse servletResponse,
        ClientCache clientCache,
        ClientAuthorizationProvider authorizationProvider,
        ClientRequestContextService clientRequestContextService,
        ClientZoneRenderer clientZoneRenderer,
        ClientEventPersonProviderFactory personProviderFactory) {
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.clientCache = clientCache;
        this.authorizationProvider = authorizationProvider;
        this.clientRequestContextService = clientRequestContextService;
        this.clientZoneRenderer = clientZoneRenderer;
        this.personProviderFactory = personProviderFactory;
    }

    @Override
    public Response fetch(String accessToken, String zoneName)
        throws UserAuthorizationRestException, ClientZoneRestException {
        return render(accessToken,
            ClientRenderZoneRequest.builder()
                .withZoneName(zoneName)
                .build());
    }

    @Override
    public Response fetch(String accessToken) throws UserAuthorizationRestException, ClientZoneRestException {
        return render(accessToken,
            ClientRenderZoneRequest.builder()
                .build());
    }

    @Override
    public Response post(String accessToken, Optional<ClientRenderZoneRequest> request)
        throws UserAuthorizationRestException, ClientZoneRestException {
        return render(accessToken, request.orElseGet(
            () -> ClientRenderZoneRequest.builder()
                .build()));
    }

    @Override
    public Response post(String accessToken, String zoneName, Optional<UnnamedClientRenderZoneRequest> request)
        throws UserAuthorizationRestException, ClientZoneRestException {
        Map<String, Object> data = request.map(value -> value.getData()).orElse(ImmutableMap.of());

        return render(accessToken, ClientRenderZoneRequest.builder()
            .withData(data)
            .withZoneName(zoneName)
            .build());
    }

    @WithSpan
    private Response render(String accessToken, ClientRenderZoneRequest request)
        throws UserAuthorizationRestException, ClientZoneRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        if (StringUtils.isBlank(request.getZoneName())) {
            throw RestExceptionBuilder.newBuilder(ClientZoneRestException.class)
                .withErrorCode(ClientZoneRestException.MISSING_ZONE_NAME)
                .build();
        }

        Optional<String> eventTimeValue =
            JsonMap.valueOf(request.getData())
                .getValueAsString(ClientRenderZoneRequest.DATA_EVENT_TIME);

        if (eventTimeValue.isPresent()) {
            validateEventTime(authorization.getClientId(), eventTimeValue.get());
        }

        ClientRequestContext requestContext;
        try {
            requestContext = clientRequestContextService
                .createBuilder(authorization, servletRequest)
                .withEventName(request.getZoneName())
                .withApiType(ClientApiType.CLIENT_ZONE)
                .withEventProcessing(configurator -> {
                    request.getData().forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                    });
                    configurator.addPrehandler(personProviderFactory.newPersonPrehandler(authorization));
                    configurator.addPrehandler(personProviderFactory.newPersonKeyUpdatePrehandler(authorization));
                })
                .withCandidateProvider(personProviderFactory.newPersonProvider(authorization))
                .build();
        } catch (EventProcessorException e) {
            Throwable cause = e.getCause();
            if (cause != null
                && PersonLockAcquireRuntimeException.class.isAssignableFrom(cause.getClass())) {
                throw (PersonLockAcquireRuntimeException) cause;
            }

            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }

        ClientZoneRenderResponse renderResponse;
        try {
            renderResponse =
                clientZoneRenderer.render(requestContext, servletRequest, ZoneMetrics.CLIENT_ZONE_DURATION);
        } catch (PersonLockAcquireException e) {
            throw e.getCause();
        } catch (ZoneInvalidRedirectException e) {
            addResponseHeadersExcludingLocationHeader(e.getZoneResponse().getHeaders());

            throw RestExceptionBuilder.newBuilder(ClientZoneRestException.class)
                .withErrorCode(ClientZoneRestException.INVALID_REDIRECT)
                .addParameter("invalid_redirect_urls", e.getInvalidRedirectUrls())
                .withCause(e)
                .build();
        }

        if (renderResponse.getWebResponse().getStatus() == Status.FOUND.getStatusCode()) {
            return renderResponse.getWebResponse();
        }

        if (!hasStepEvent(renderResponse)) {
            MultivaluedMap<String, Object> responseHeaders = renderResponse.getWebResponse().getHeaders();
            addResponseHeaders(responseHeaders);

            throw RestExceptionBuilder.newBuilder(ClientZoneRestException.class)
                .withErrorCode(ClientZoneRestException.NO_CREATIVE)
                .build();
        }

        return renderResponse.getWebResponse();
    }

    private void validateEventTime(Id<ClientHandle> clientId, String eventTime)
        throws ClientZoneRestException, UserAuthorizationRestException {
        try {
            new DateTimeBuilder()
                .withDateString(eventTime)
                .withDefaultTimezone(getTimeZone(clientId))
                .build()
                .toInstant();
        } catch (DateTimeParseException | DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(ClientZoneRestException.class)
                .withErrorCode(ClientZoneRestException.INVALID_TIME_FORMAT)
                .addParameter("time", eventTime)
                .withCause(e)
                .build();
        }
    }

    private ZoneId getTimeZone(Id<ClientHandle> clientId)
        throws ClientZoneRestException, UserAuthorizationRestException {
        Optional<String> timeZone =
            Optional.ofNullable(servletRequest.getHeader(ExtoleHeaderType.TIME_ZONE.getHeaderName()));
        if (timeZone.isPresent()) {
            try {
                return ZoneId.of(timeZone.get());
            } catch (DateTimeException e) {
                throw RestExceptionBuilder.newBuilder(ClientZoneRestException.class)
                    .withErrorCode(ClientZoneRestException.INVALID_TIME_ZONE)
                    .addParameter("time_zone", timeZone.get())
                    .withCause(e)
                    .build();
            }
        }

        try {
            return clientCache.getById(clientId).getTimeZone();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    private static boolean hasStepEvent(ClientZoneRenderResponse renderResponse) {
        return renderResponse.getZoneRenderResponse().getZoneEvents().stream()
            .map(ZoneEventResult::getStepEvent)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .isPresent();
    }

    private void addResponseHeaders(MultivaluedMap<String, Object> responseHeaders) {
        for (Entry<String, List<Object>> entry : responseHeaders.entrySet()) {
            String key = entry.getKey();
            List<Object> values = entry.getValue();

            for (Object value : values) {
                servletResponse.addHeader(key, String.valueOf(value));
            }
        }
    }

    private void addResponseHeadersExcludingLocationHeader(Multimap<String, String> headers) {
        for (Entry<String, String> entry : headers.entries()) {
            String key = entry.getKey();

            if (key.equalsIgnoreCase(ZoneRenderResponse.HEADER_LOCATION)) {
                continue;
            }

            String value = entry.getValue();
            servletResponse.addHeader(key, value);
        }
    }

}
