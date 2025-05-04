package com.extole.common.rest.support.exception;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.JacksonExceptionRestTranslator;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

@Provider
@Priority(1)
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMappingExceptionMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOG);
    private static final JacksonExceptionRestTranslator JACKSON_EXCEPTION_TRANSLATOR =
        new JacksonExceptionRestTranslator();

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(JsonMappingException exception) {
        RestException restException = JACKSON_EXCEPTION_TRANSLATOR.translate(exception);

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
