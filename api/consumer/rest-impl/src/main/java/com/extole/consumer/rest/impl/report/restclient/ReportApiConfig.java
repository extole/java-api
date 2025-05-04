package com.extole.consumer.rest.impl.report.restclient;

import javax.ws.rs.client.Client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.common.rest.client.JerseyClientConfigBuilder;

@Configuration
public class ReportApiConfig {

    private static final String METRIC_NAME = "report.api.requests";

    @Bean
    public Client reportApiClient(ReportApiProperties properties, ExtoleMetricRegistry metricRegistry) {

        Client client = JerseyClientConfigBuilder.newConfig()
            .withMaxTotal(properties.getConnectionPoolSize())
            .withDefaultMaxPerRoute(properties.getConnectionPoolSize())
            .withConnectionTimeout(properties.getConnectionTimeout())
            .withConnectionTtl(properties.getConnectionTtl())
            .withReadTimeout(properties.getReadTimeout())
            .build();

        return client
            .register(new MetricsClientFilter(metricRegistry, METRIC_NAME))
            .register(new ReportApiErrorHandler());
    }
}
