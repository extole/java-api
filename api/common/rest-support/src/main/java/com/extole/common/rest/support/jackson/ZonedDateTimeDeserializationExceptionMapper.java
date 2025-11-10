package com.extole.common.rest.support.jackson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.lang.date.ZonedDateTimeDeserializationException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

// TODO refactor as port of ENG-15581
public class ZonedDateTimeDeserializationExceptionMapper
    implements ExceptionMapper<ZonedDateTimeDeserializationException> {

    private static final Logger LOG = LoggerFactory.getLogger(ZonedDateTimeDeserializationExceptionMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOG);

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(ZonedDateTimeDeserializationException exception) {
        RestException restException = RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
            .withErrorCode(WebApplicationRestRuntimeException.INVALID_DATE_TIME)
            .withCause(exception)
            .addParameter("value", exception.getDateString())
            .build();

        EXCEPTION_LOGGER.log(restException, request);

        RestExceptionResponse response = new RestExceptionResponseBuilder()
            .withUniqueId(String.valueOf(restException.getUniqueId()))
            .withHttpStatusCode(restException.getHttpStatusCode())
            .withCode(restException.getErrorCode())
            .withMessage(restException.getMessage())
            .withParameters(restException.getParameters())
            .build();

        return Response.status(response.getHttpStatusCode()).entity(response).type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
