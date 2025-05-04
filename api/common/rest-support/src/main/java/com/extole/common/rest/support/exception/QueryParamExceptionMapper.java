package com.extole.common.rest.support.exception;

import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ParamException;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

@Provider
@Priority(1)
public class QueryParamExceptionMapper implements ExceptionMapper<ParamException.QueryParamException> {
    @Override
    public Response toResponse(ParamException.QueryParamException exception) {
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withCause(exception)
                .withErrorCode(WebApplicationRestRuntimeException.BINDING_ERROR)
                .addParameter("argument", exception.getParameterName())
                .build();

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
