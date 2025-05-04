package com.extole.client.zone.rest.impl;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.common.client.pod.ClientPodService;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.person.service.profile.PersonLockAcquireRuntimeException;

@Provider
@Priority(1)
public final class PersonLockAcquireRuntimeExceptionMapper
    implements ExceptionMapper<PersonLockAcquireRuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonLockAcquireRuntimeExceptionMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOGGER);

    private final HttpServletRequest request;
    private final ClientPodService podService;

    @Autowired
    public PersonLockAcquireRuntimeExceptionMapper(
        @Context HttpServletRequest request,
        ClientPodService podService) {
        this.request = request;
        this.podService = podService;
    }

    @Override
    public Response toResponse(PersonLockAcquireRuntimeException exception) {
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.TOO_MANY_REQUESTS)
                .withCause(exception)
                .build();

        EXCEPTION_LOGGER.log(restException, request, podService.getCurrentPod().getName());

        RestExceptionResponse response = new RestExceptionResponseBuilder()
            .withUniqueId(String.valueOf(restException.getUniqueId()))
            .withHttpStatusCode(restException.getHttpStatusCode())
            .withCode(restException.getErrorCode())
            .withMessage(restException.getMessage())
            .withParameters(restException.getParameters())
            .build();

        return Response.status(response.getHttpStatusCode())
            .entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
