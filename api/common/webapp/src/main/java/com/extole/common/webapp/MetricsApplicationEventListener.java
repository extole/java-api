package com.extole.common.webapp;

import java.util.Arrays;
import java.util.List;

import com.codahale.metrics.Clock;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.metrics.ExtoleMetricRegistry;

@Component
public final class MetricsApplicationEventListener implements ApplicationEventListener {

    private final ExtoleMetricRegistry metricRegistry;
    private final String metricName;
    private final Clock clock;
    private final List<String> detailedClientShortNames;
    private final List<CustomRequestMetricProvider> customRequestMetricProviders;

    public MetricsApplicationEventListener(ExtoleMetricRegistry metricRegistry,
        @Value("${metrics.web.server.requests.metric.name:http.server.requests}") String metricName,
        @Value("${metrics.web.server.requests.detailed.client.short.names:extole}") String[] detailedClientShortNames,
        @Autowired List<CustomRequestMetricProvider> customRequestMetricProviders) {
        this.metricRegistry = metricRegistry;
        this.metricName = metricName;
        this.clock = Clock.defaultClock();
        this.detailedClientShortNames = Arrays.asList(detailedClientShortNames);
        this.customRequestMetricProviders = customRequestMetricProviders;
    }

    @Override
    public void onEvent(ApplicationEvent event) {

    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new MetricsRequestEventListener(metricRegistry, metricName, clock, detailedClientShortNames,
            customRequestMetricProviders);
    }
}
