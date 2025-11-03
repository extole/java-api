package com.extole.common.webapp;

import org.glassfish.jersey.server.monitoring.RequestEvent;

public interface CustomRequestMetricProvider {

    boolean shouldConsider(RequestEvent event);

    String computeMetricName(RequestEvent event,
        String metricName,
        String method,
        String uri,
        String statusCodeFamily,
        String httpCode,
        String clientShortName);

}
