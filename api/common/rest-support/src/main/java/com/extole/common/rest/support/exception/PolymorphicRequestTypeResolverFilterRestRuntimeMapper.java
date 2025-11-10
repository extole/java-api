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

import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

@Provider
@Priority(1)
public class PolymorphicRequestTypeResolverFilterRestRuntimeMapper
    implements ExceptionMapper<PolymorphicRequestTypeResolverFilterRestWrapperException> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(PolymorphicRequestTypeResolverFilterRestRuntimeMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOGGER);

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(PolymorphicRequestTypeResolverFilterRestWrapperException exception) {
        RestExceptionResponse response = new RestExceptionResponseBuilder(exception.getCause()).build();
        EXCEPTION_LOGGER.log(exception.getCause(), request);
        return Response.status(response.getHttpStatusCode()).entity(response).type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
