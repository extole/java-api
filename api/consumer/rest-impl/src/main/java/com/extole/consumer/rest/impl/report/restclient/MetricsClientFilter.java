package com.extole.consumer.rest.impl.report.restclient;

import static java.util.Collections.synchronizedMap;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.metrics.ExtoleTimer;

@Priority(Integer.MAX_VALUE)
public final class MetricsClientFilter implements ClientRequestFilter, ClientResponseFilter {

    private final Map<ClientRequestContext, ExtoleTimer.Context> timers = synchronizedMap(new IdentityHashMap<>());

    private final ExtoleMetricRegistry metricRegistry;
    private final String metricName;

    public MetricsClientFilter(ExtoleMetricRegistry registry, String metricName) {
        this.metricRegistry = registry;
        this.metricName = metricName;
    }

    @Override
    public void filter(ClientRequestContext requestContext) {
        ExtoleTimer.Context timer =
            metricRegistry
                .timer(metricName + "." + requestContext.getMethod() + "." + requestContext.getUri().getPath())
                .time();

        timers.put(requestContext, timer);
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
        timers.remove(requestContext).stop();
    }
}
