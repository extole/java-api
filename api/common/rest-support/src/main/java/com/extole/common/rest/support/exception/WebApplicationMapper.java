package com.extole.common.rest.support.exception;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.RestExceptionLogger;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

@Provider
@Priority(1)
public class WebApplicationMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger LOG = LoggerFactory.getLogger(WebApplicationMapper.class);
    private static final int HTTP_STATUS_400 = 400;
    private static final int HTTP_STATUS_401 = 401;
    private static final int HTTP_STATUS_403 = 403;
    private static final int HTTP_STATUS_404 = 404;
    private static final int HTTP_STATUS_405 = 405;
    private static final int HTTP_STATUS_415 = 415;
    private static final int HTTP_STATUS_500 = 500;

    private static final RestExceptionLogger EXCEPTION_LOGGER = new RestExceptionLogger(LOG);

    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(WebApplicationException webException) {
        int httpStatus = webException.getResponse().getStatus();

        RestException exception;
        if (httpStatus >= HTTP_STATUS_400 && httpStatus < HTTP_STATUS_500) {
            RestExceptionBuilder<WebApplicationRestRuntimeException> builder =
                RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                    .withCause(webException);

            if (httpStatus == HTTP_STATUS_401) {
                builder.withErrorCode(WebApplicationRestRuntimeException.UNAUTHORIZED);
            } else if (httpStatus == HTTP_STATUS_403) {
                builder.withErrorCode(WebApplicationRestRuntimeException.FORBIDDEN);
            } else if (httpStatus == HTTP_STATUS_404) {
                NotFoundException error = (NotFoundException) webException;
                if (!Strings.isNullOrEmpty(error.getResponse().getHeaderString("X-Error-Page"))) {
                    return error.getResponse();
                }
                builder.withErrorCode(WebApplicationRestRuntimeException.NOT_FOUND);
            } else if (httpStatus == HTTP_STATUS_405) {
                builder.withErrorCode(WebApplicationRestRuntimeException.NOT_ALLOWED);
            } else if (httpStatus == HTTP_STATUS_415) {
                builder.withErrorCode(WebApplicationRestRuntimeException.UNSUPPORTED_MEDIA_TYPE);
            } else {
                builder.withErrorCode(WebApplicationRestRuntimeException.UNEXPECTED_REQUEST);
            }

            exception = builder.build();
        } else {
            exception = RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(webException)
                .build();
        }

        EXCEPTION_LOGGER.log(exception, request);

        RestExceptionResponse response = new RestExceptionResponseBuilder()
            .withUniqueId(String.valueOf(exception.getUniqueId()))
            .withHttpStatusCode(exception.getHttpStatusCode())
            .withCode(exception.getErrorCode())
            .withMessage(exception.getMessage())
            .withParameters(exception.getParameters())
            .build();

        return Response.status(response.getHttpStatusCode()).entity(response).type(MediaType.APPLICATION_JSON_TYPE)
            .build();
    }
}
