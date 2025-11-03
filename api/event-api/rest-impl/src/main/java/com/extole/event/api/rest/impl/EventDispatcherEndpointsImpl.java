package com.extole.event.api.rest.impl;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.producer.KafkaSendEventFailureException;
import com.extole.common.lang.JsonMap;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.rest.ExtoleHeaderType;
import com.extole.common.rest.exception.RequestBodySizeTooLargeRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.TooManyRequestsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.filter.CachedBodyHttpServletRequestWrapper;
import com.extole.event.api.rest.EventDispatcherEndpoints;
import com.extole.event.api.rest.EventDispatcherRequest;
import com.extole.event.api.rest.EventDispatcherResponse;
import com.extole.event.api.rest.EventDispatcherRestException;
import com.extole.event.api.rest.UnnamedEventDispatcherRequest;
import com.extole.event.raw.ApiHandler;
import com.extole.event.raw.ClientRawEventProducer;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;

@Provider
public class EventDispatcherEndpointsImpl implements EventDispatcherEndpoints {
    private static final String DATA_EVENT_TIME = "event_time";

    private final HttpServletRequest servletRequest;
    private final ClientCache clientCache;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientRawEventProducer eventProducer;

    @Inject
    public EventDispatcherEndpointsImpl(@Context HttpServletRequest servletRequest,
        ClientCache clientCache,
        ClientAuthorizationProvider authorizationProvider,
        ClientRawEventProducer eventProducer) {
        this.servletRequest = servletRequest;
        this.clientCache = clientCache;
        this.authorizationProvider = authorizationProvider;
        this.eventProducer = eventProducer;
    }

    @Override
    public EventDispatcherResponse submit(String accessToken, EventDispatcherRequest request)
        throws UserAuthorizationRestException, EventDispatcherRestException, TooManyRequestsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (Strings.isNullOrEmpty(request.getEventName())) {
            throw RestExceptionBuilder.newBuilder(EventDispatcherRestException.class)
                .withErrorCode(EventDispatcherRestException.MISSING_EVENT_NAME)
                .build();
        }
        if (request.getEventTime().isPresent()) {
            validateEventTime(authorization.getClientId(), request.getEventTime().get());
        }
        JsonMap data = JsonMap.valueOf(request.getData());
        Optional<String> eventTimeValue = data.getValueAsString(DATA_EVENT_TIME);
        if (eventTimeValue.isPresent()) {
            validateEventTime(authorization.getClientId(), eventTimeValue.get());
        }
        return sendEvent(authorization, ApiHandler.V5_EVENTS, request.getData());
    }

    @Override
    public EventDispatcherResponse submit(String accessToken, String eventName, UnnamedEventDispatcherRequest request)
        throws EventDispatcherRestException, UserAuthorizationRestException, TooManyRequestsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        if (Strings.isNullOrEmpty(eventName)) {
            throw RestExceptionBuilder.newBuilder(EventDispatcherRestException.class)
                .withErrorCode(EventDispatcherRestException.MISSING_EVENT_NAME)
                .build();
        }
        JsonMap data = JsonMap.valueOf(request.getData());
        Optional<String> eventTimeValue = data.getValueAsString(DATA_EVENT_TIME);
        if (eventTimeValue.isPresent()) {
            validateEventTime(authorization.getClientId(), eventTimeValue.get());
        }
        return sendEvent(authorization, ApiHandler.V5_EVENTS_EVENT_NAME, request.getData());
    }

    private EventDispatcherResponse sendEvent(Authorization authorization, ApiHandler apiHandler,
        Map<String, Object> data)
        throws TooManyRequestsRestException {
        try {
            CachedBodyHttpServletRequestWrapper request = (CachedBodyHttpServletRequestWrapper) servletRequest;
            String requestBody = new String(request.getHttpRequestBody(), StandardCharsets.UTF_8);
            ClientRawEventProducer.RawEventBuilder builder = eventProducer.createSynchronousBuilder()
                .withAccessToken(authorization.getAccessToken())
                .withClientId(authorization.getClientId())
                .withBody(requestBody)
                .withHttpMethod(servletRequest.getMethod())
                .withUrl(getRawEventUrl(request))
                .withApiHandler(apiHandler)
                .withData(data);

            for (String headerName : Collections.list(servletRequest.getHeaderNames())) {
                builder.addHttpHeader(headerName, Collections.list(servletRequest.getHeaders(headerName)));
            }
            if (servletRequest.getCookies() != null) {
                Arrays.stream(servletRequest.getCookies())
                    .forEach(cookie -> builder.addHttpCookie(cookie.getName(),
                        Collections.singletonList(cookie.getValue())));
            }
            return new EventDispatcherResponse(builder.sendSynchronously().getEventId().getValue());
        } catch (KafkaSendEventFailureException e) {
            throw RestExceptionBuilder.newBuilder(TooManyRequestsRestException.class)
                .withErrorCode(TooManyRequestsRestException.TOO_MANY_REQUESTS)
                .withCause(e)
                .build();
        } catch (EventTooLargeException e) {
            throw RestExceptionBuilder.newBuilder(RequestBodySizeTooLargeRestRuntimeException.class)
                .withErrorCode(RequestBodySizeTooLargeRestRuntimeException.REQUEST_BODY_TOO_LARGE)
                .addParameter("body_size", e.getEventSize())
                .addParameter("max_allowed_body_size", e.getMaxRequestSizeBytes())
                .withCause(e)
                .build();
        }
    }

    private void validateEventTime(Id<ClientHandle> clientId, String eventTime)
        throws EventDispatcherRestException, UserAuthorizationRestException {
        try {
            new DateTimeBuilder().withDateString(eventTime).withDefaultTimezone(getTimeZone(clientId)).build()
                .toInstant();
        } catch (DateTimeParseException | DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(EventDispatcherRestException.class)
                .withErrorCode(EventDispatcherRestException.INVALID_EVENT_TIME_FORMAT)
                .addParameter("event_time", eventTime)
                .withCause(e).build();
        }
    }

    private ZoneId getTimeZone(Id<ClientHandle> clientId)
        throws EventDispatcherRestException, UserAuthorizationRestException {
        Optional<String> timeZone =
            Optional.ofNullable(servletRequest.getHeader(ExtoleHeaderType.TIME_ZONE.getHeaderName()));
        if (timeZone.isPresent()) {
            try {
                return ZoneId.of(timeZone.get());
            } catch (DateTimeException e) {
                throw RestExceptionBuilder.newBuilder(EventDispatcherRestException.class)
                    .withErrorCode(EventDispatcherRestException.INVALID_TIME_ZONE)
                    .addParameter("time_zone", timeZone.get())
                    .withCause(e).build();
            }
        }
        try {
            return clientCache.getById(clientId).getTimeZone();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    private String getRawEventUrl(HttpServletRequest servletRequest) {
        String incomingUrlFromHeaders = servletRequest.getHeader("X-Extole-Incoming-Url");

        if (StringUtils.isNotBlank(incomingUrlFromHeaders)) {
            return incomingUrlFromHeaders;
        }

        return servletRequest.getRequestURL().toString();
    }

}
