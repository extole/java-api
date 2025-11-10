package com.extole.common.rest.support.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.logstash.logback.marker.MapEntriesAppendingMarker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.extole.common.lang.ExtoleThreadFactory;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.metrics.ExtoleCounter;
import com.extole.common.metrics.ExtoleHistogram;
import com.extole.common.metrics.ExtoleMetricRegistry;

@Provider
public class LoggingServletRequestFilter implements ContainerResponseFilter, ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServletRequestFilter.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();
    private static final String REQUEST_START_TIME = "request_start_time";
    private static final String OTHER_METRIC = "other";

    private final int requestProcessingWarnThreshold;
    private final ExecutorService executorService;
    private final int responseMaxLength;
    private final ExtoleMetricRegistry metricRegistry;

    private Map<String, ExtoleCounter> counters;
    private Map<String, ExtoleHistogram> histograms;

    @Autowired
    public LoggingServletRequestFilter(
        @Value("${request.processing.warn.threshold.ms:100}") int requestProcessingWarnThreshold,
        @Value("${request.processing.pool.size:2}") int requestProcessingThreadPoolSize,
        @Value("${request.processing.logged.response.max.length:2000}") int responseMaxLength,
        ExtoleMetricRegistry metricRegistry) {
        this.requestProcessingWarnThreshold = requestProcessingWarnThreshold;
        this.executorService = Executors.newFixedThreadPool(requestProcessingThreadPoolSize,
            new ExtoleThreadFactory(LoggingServletRequestFilter.class.getSimpleName()));
        this.responseMaxLength = responseMaxLength;
        this.metricRegistry = metricRegistry;

        initializeMetrics();
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(REQUEST_START_TIME, Long.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext != null && requestContext.getProperty(REQUEST_START_TIME) != null) {
            long requestProcessingTime =
                System.currentTimeMillis() - ((Long) requestContext.getProperty(REQUEST_START_TIME)).longValue();
            String method = requestContext.getMethod();
            if (requestProcessingTime > requestProcessingWarnThreshold) {
                try {
                    ServletDetails details = new ServletDetails(requestContext, responseContext);
                    executorService.submit(() -> {
                        try {
                            Map<String, String> logMap = new HashMap<>();
                            logMap.put("duration", String.valueOf(requestProcessingTime) + "ms");
                            logMap.put("method", details.getMethod());
                            logMap.put("request_uri", details.getRequestUri());
                            logMap.put("request_headers", logHeaders(details.getRequestHeaders()));
                            logMap.put("response_headers", logHeaders(details.getResponseHeaders()));
                            logMap.put("response_status", String.valueOf(details.getResponseStatus()));
                            logMap.put("response_entity", logResponseEntity(details));
                            details.getAccessToken().ifPresent(accessToken -> logMap.put("access_token", accessToken));
                            LOG.info(new MapEntriesAppendingMarker(logMap), "Request exceeded warn threshold: {}",
                                String.valueOf(requestProcessingWarnThreshold));
                            updateMetrics(details.getMethod(), requestProcessingTime);
                        } catch (Exception e) {
                            LOG.error("Unable to build and log details for request {}.", details.getRequestUri(), e);
                        }
                    });
                } catch (Throwable e) {
                    // Catching all exceptions here as we don't want to propagate failures in our filters.
                    LOG.error("Unable to extract log details.", e);
                }
            } else {
                updateMetrics(method, requestProcessingTime);
            }
        }
    }

    private String logHeaders(Map<String, List<String>> headers) {
        StringBuilder logMessage = new StringBuilder();
        for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
            for (String headerValue : headerEntry.getValue()) {
                logMessage.append(headerEntry.getKey()).append(": ").append(headerValue).append("\n");
            }
        }
        return logMessage.toString();
    }

    private String logResponseEntity(ServletDetails details) {
        String response;
        if (details.getResponseEntity() instanceof String) {
            response = (String) details.getResponseEntity();
        } else {
            try {
                response = OBJECT_MAPPER.writeValueAsString(details.getResponseEntity());
            } catch (JsonProcessingException e) {
                response = String.valueOf(details.getResponseEntity());
            }
        }
        return StringUtils.abbreviate(response, responseMaxLength);
    }

    private void updateMetrics(String method, long requestProcessingTime) {
        counters.get(buildMetricName(method)).increment();
        histograms.get(buildHistogramMetricName(method)).update(requestProcessingTime);
    }

    private void initializeMetrics() {
        metricRegistry.counter(buildMetricName(HttpMethod.POST));
        metricRegistry.counter(buildMetricName(HttpMethod.PUT));
        metricRegistry.counter(buildMetricName(HttpMethod.GET));
        metricRegistry.counter(buildMetricName(HttpMethod.DELETE));
        metricRegistry.counter(buildMetricName(OTHER_METRIC));

        metricRegistry.histogram(buildHistogramMetricName(HttpMethod.POST));
        metricRegistry.histogram(buildHistogramMetricName(HttpMethod.PUT));
        metricRegistry.histogram(buildHistogramMetricName(HttpMethod.GET));
        metricRegistry.histogram(buildHistogramMetricName(HttpMethod.DELETE));
        metricRegistry.histogram(buildHistogramMetricName(OTHER_METRIC));

        counters = metricRegistry.getCounters();
        histograms = metricRegistry.getHistograms();
    }

    private String buildMetricName(String method) {
        switch (method.toUpperCase()) {
            case HttpMethod.POST:
            case HttpMethod.PUT:
            case HttpMethod.GET:
            case HttpMethod.DELETE:
                return method.toLowerCase();
            default:
                return OTHER_METRIC;
        }
    }

    private String buildHistogramMetricName(String method) {
        return buildMetricName(method) + ".duration";
    }
}
