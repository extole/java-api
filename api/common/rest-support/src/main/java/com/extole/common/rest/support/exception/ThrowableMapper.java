package com.extole.common.rest.support.exception;

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
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.exception.ExtoleRestRuntimeException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

@Provider
@Priority(1)
public class ThrowableMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOGGER);

    private final HttpServletRequest request;
    private final ClientPodService podService;

    @Autowired
    public ThrowableMapper(@Context HttpServletRequest request,
        ClientPodService podService) {
        this.request = request;
        this.podService = podService;
    }

    @Override
    public Response toResponse(Throwable throwable) {
        RestExceptionResponse response;

        if (throwable instanceof ExtoleRestException) {
            ExtoleRestException exception = (ExtoleRestException) throwable;
            response = new RestExceptionResponseBuilder(exception).build();
            EXCEPTION_LOGGER.log(exception, request, podService.getCurrentPod().getName());
        } else if (throwable instanceof ExtoleRestRuntimeException) {
            ExtoleRestRuntimeException exception = (ExtoleRestRuntimeException) throwable;
            response = new RestExceptionResponseBuilder(exception).build();
            EXCEPTION_LOGGER.log(exception, request, podService.getCurrentPod().getName());
        } else {
            FatalRestRuntimeException exception = RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(throwable).build();
            response = new RestExceptionResponseBuilder(exception).build();
            EXCEPTION_LOGGER.log(exception, request, podService.getCurrentPod().getName());
        }

        return Response.status(response.getHttpStatusCode())
            .entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
