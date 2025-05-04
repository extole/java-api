package com.extole.common.rest.timezone;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

public final class TimeZoneParamExceptionMapper
    implements ExceptionMapper<TimeZoneParamException> {

    @Override
    public Response toResponse(TimeZoneParamException exception) {
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withCause(exception)
                .withErrorCode(WebApplicationRestRuntimeException.INVALID_TIMEZONE)
                .addParameter("argument", exception.getParameterName())
                .addParameter("value", exception.getDefaultStringValue())
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
