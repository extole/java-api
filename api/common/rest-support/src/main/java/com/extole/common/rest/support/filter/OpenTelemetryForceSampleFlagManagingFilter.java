package com.extole.common.rest.support.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenTelemetryForceSampleFlagManagingFilter extends OncePerRequestFilter {

    public static final String X_EXTOLE_TRACE_ID = "X-Extole-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        response.addHeader(X_EXTOLE_TRACE_ID, Span.current().getSpanContext().getTraceId());
        filterChain.doFilter(request, response);
    }
}
