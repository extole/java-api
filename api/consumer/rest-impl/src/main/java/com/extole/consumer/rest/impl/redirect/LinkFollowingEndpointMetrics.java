package com.extole.consumer.rest.impl.redirect;

import java.util.Optional;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.id.Id;
import com.extole.model.service.metrics.MetricConfiguration;

public enum LinkFollowingEndpointMetrics {
    LINK_FOLLOWING_DURATION("full.duration.ms"),

    HANDLE_SHAREABLE_DURATION("handle.shareable.duration.ms"),
    LOOKUP_SHAREABLE_DURATION("lookup.shareable.duration.ms"),
    RENDER_SHAREABLE_DURATION("render.shareable.duration.ms"),

    HANDLE_PROMOTABLE_DURATION("handle.promotable.duration.ms"),
    LOOKUP_PROMOTABLE_DURATION("lookup.promotable.duration.ms"),
    RENDER_PROMOTABLE_DURATION("render.promotable.duration.ms"),

    RENDER_CUSTOM_NOT_FOUND_DURATION("render.custom_not_found.duration.ms");

    private static final String METRIC_PREFIX = "link.following";

    private final String metricName;
    private final String fullMetricName;

    LinkFollowingEndpointMetrics(String metricName) {
        this.metricName = metricName;
        this.fullMetricName = LinkFollowingEndpointMetrics.METRIC_PREFIX + "." + metricName;
    }

    public void updateHistogram(ExtoleMetricRegistry metricRegistry, MetricConfiguration metricConfiguration,
        long histogramValue, Optional<Id<ClientHandle>> clientId) {
        metricRegistry.histogram(fullMetricName).update(histogramValue);
        if (clientId.isPresent() && metricConfiguration.isClientMonitored(clientId.get())) {
            String clientHistogramName = METRIC_PREFIX + ".client." + clientId.get().getValue() + "." + metricName;
            metricRegistry.histogram(clientHistogramName).update(histogramValue);
        }
    }
}
