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

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.common.rest.omissible.OmissibleInvalidNullException;
import com.extole.common.rest.omissible.OmissibleRestException;

@Provider
@Priority(1)
public class OmissibleInvalidNullExceptionMapper implements ExceptionMapper<OmissibleInvalidNullException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmissibleInvalidNullExceptionMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOGGER);

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(OmissibleInvalidNullException exception) {

        OmissibleRestException restException = RestExceptionBuilder.newBuilder(OmissibleRestException.class)
            .withErrorCode(OmissibleRestException.INVALID_NULL)
            .withCause(exception)
            .addParameter("attribute_name", exception.getPropertyName().getSimpleName())
            .build();
        EXCEPTION_LOGGER.log(restException, request);

        RestExceptionResponse response = new RestExceptionResponseBuilder(restException).build();

        return Response.status(response.getHttpStatusCode())
            .entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
