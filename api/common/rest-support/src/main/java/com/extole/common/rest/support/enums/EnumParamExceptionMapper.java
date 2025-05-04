package com.extole.common.rest.support.enums;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

public class EnumParamExceptionMapper implements ExceptionMapper<EnumParamException> {

    @Override
    public Response toResponse(EnumParamException exception) {
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withCause(exception)
                .withErrorCode(WebApplicationRestRuntimeException.UNSUPPORTED_ENUM_TYPE)
                .addParameter("value", exception.getProvidedValue())
                .addParameter("allowed_values", exception.getAllowedValues())
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
