package com.extole.common.rest.support.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.internal.process.MappableException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.WebApplicationRestRuntimeException;
import com.extole.common.rest.model.RestExceptionResponseBuilder;

public class CachedBodyHttpServletRequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            doFilter(new CachedBodyHttpServletRequestWrapper(request), response, filterChain);
        } catch (ServletException e) {
            if (e.getCause() instanceof MappableException &&
                e.getCause().getCause() instanceof NotAcceptableException) {
                respondWithNotAcceptable(response, e);
            } else if (e.getCause() instanceof IllegalArgumentException &&
                e.getCause().getMessage().startsWith("Malformed percent-encoded octet")) {
                respondWithInvalidUrl(request, response, e);
            } else {
                throw e;
            }
        }
    }

    private void respondWithNotAcceptable(HttpServletResponse response, Exception cause)
        throws IOException {
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.NOT_ACCEPTABLE)
                .withCause(cause)
                .build();

        respondWithRestException(response, restException);
    }

    private void respondWithInvalidUrl(HttpServletRequest request, HttpServletResponse response, Exception cause)
        throws IOException {
        String incomingUrl = request.getQueryString() == null ? request.getRequestURL().toString()
            : request.getRequestURL().append("?").append(request.getQueryString()).toString();
        WebApplicationRestRuntimeException restException =
            RestExceptionBuilder.newBuilder(WebApplicationRestRuntimeException.class)
                .withErrorCode(WebApplicationRestRuntimeException.INVALID_URI)
                .addParameter("incoming_url", incomingUrl)
                .withCause(cause)
                .build();

        respondWithRestException(response, restException);
    }

    private void respondWithRestException(HttpServletResponse response,
        WebApplicationRestRuntimeException restException) throws IOException {
        response.resetBuffer();
        response.setStatus(restException.getHttpStatusCode());
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        response.getOutputStream().print(new RestExceptionResponseBuilder(restException).build().toString());
        response.flushBuffer();
    }
}
