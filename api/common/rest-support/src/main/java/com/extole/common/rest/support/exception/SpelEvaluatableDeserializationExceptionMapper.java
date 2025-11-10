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
import com.extole.common.rest.expression.EvaluatableRestException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.evaluateable.spel.SpelEvaluatableDeserializationException;

@Provider
@Priority(1)
public class SpelEvaluatableDeserializationExceptionMapper
    implements ExceptionMapper<SpelEvaluatableDeserializationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpelEvaluatableDeserializationExceptionMapper.class);
    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOGGER);

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(SpelEvaluatableDeserializationException exception) {

        EvaluatableRestException restException = RestExceptionBuilder.newBuilder(EvaluatableRestException.class)
            .withErrorCode(EvaluatableRestException.SPEL_EXPRESSION_INVALID_SYNTAX)
            .withCause(exception)
            .addParameter("description", exception.getOriginalMessage())
            .build();
        EXCEPTION_LOGGER.log(restException, request);

        RestExceptionResponse response = new RestExceptionResponseBuilder(restException).build();

        return Response.status(response.getHttpStatusCode())
            .entity(response)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
