package com.extole.common.rest.support.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.springframework.web.filter.OncePerRequestFilter;

import com.extole.telemetry.context.ContextKeys;

public class OpenTelemetryForceSampleFlagManagingFilter extends OncePerRequestFilter {

    public static final String X_EXTOLE_DEBUG = "X-Extole-Debug";
    public static final String X_EXTOLE_TRACE_ID = "X-Extole-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        boolean forceSampling = Boolean.parseBoolean(request.getHeader(X_EXTOLE_DEBUG));
        try (Scope ignored = Context.current().with(ContextKeys.FORCE_SAMPLING, forceSampling).makeCurrent()) {
            response.addHeader(X_EXTOLE_TRACE_ID, Span.current().getSpanContext().getTraceId());
            filterChain.doFilter(request, response);
        }
    }
}
