package com.extole.event.api.rest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.common.event.kafka.EventTooLargeException;
import com.extole.common.event.kafka.producer.KafkaSendEventFailureException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RequestBodySizeTooLargeRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.TooManyRequestsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.event.api.rest.BatchEventDispatcherEndpoints;
import com.extole.event.api.rest.BatchEventDispatcherResponse;
import com.extole.event.api.rest.EventDispatcherRequest;
import com.extole.event.raw.ApiHandler;
import com.extole.event.raw.ClientRawEventProducer;

@Provider
public class BatchEventDispatcherEndpointsImpl implements BatchEventDispatcherEndpoints {

    private final HttpServletRequest servletRequest;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientRawEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    @Inject
    public BatchEventDispatcherEndpointsImpl(@Context HttpServletRequest servletRequest,
        javax.inject.Provider<ObjectMapper> objectMapperProvider,
        ClientAuthorizationProvider authorizationProvider,
        ClientRawEventProducer eventProducer) {
        this.servletRequest = servletRequest;
        this.objectMapper = objectMapperProvider.get();
        this.authorizationProvider = authorizationProvider;
        this.eventProducer = eventProducer;
    }

    @Override
    public List<BatchEventDispatcherResponse> batchSubmit(String accessToken,
        List<EventDispatcherRequest> requests)
        throws UserAuthorizationRestException, TooManyRequestsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        List<BatchEventDispatcherResponse> batchEventResponses = new ArrayList<>();
        for (EventDispatcherRequest request : requests) {
            batchEventResponses.add(sendEvent(authorization, request));
        }
        return batchEventResponses;
    }

    private BatchEventDispatcherResponse sendEvent(Authorization authorization, EventDispatcherRequest request)
        throws TooManyRequestsRestException {
        try {
            ClientRawEventProducer.RawEventBuilder builder = eventProducer.createSynchronousBuilder()
                .withAccessToken(authorization.getAccessToken())
                .withClientId(authorization.getClientId())
                .withBody(objectMapper.writeValueAsString(request))
                .withHttpMethod(servletRequest.getMethod())
                .withUrl(getRawEventUrl())
                .withApiHandler(ApiHandler.V5_EVENTS)
                .withData(request.getData());

            for (String headerName : Collections.list(servletRequest.getHeaderNames())) {
                builder.addHttpHeader(headerName, Collections.list(servletRequest.getHeaders(headerName)));
            }

            return BatchEventDispatcherResponse.newBuilder(request)
                .withSuccess(builder.sendSynchronously().getEventId().getValue())
                .build();
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
        } catch (JsonProcessingException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private String getRawEventUrl() {
        // TODO https://extole.atlassian.net/browse/ENG-25077
        if (Boolean.parseBoolean(System.getProperty("local.dev.env"))) {
            String incomingHostHeader = servletRequest.getHeader("host");
            String incomingXEnvoyOriginalPath = servletRequest.getHeader("x-envoy-original-path");

            if (StringUtils.isNotBlank(incomingHostHeader) && StringUtils.isNotBlank(incomingXEnvoyOriginalPath)) {
                return "https://" + incomingHostHeader + incomingXEnvoyOriginalPath;
            }
        }

        String incomingUrlFromHeaders = servletRequest.getHeader("X-Extole-Incoming-Url");

        if (StringUtils.isNotBlank(incomingUrlFromHeaders)) {
            return incomingUrlFromHeaders;
        }

        return servletRequest.getRequestURL().toString();
    }

}
