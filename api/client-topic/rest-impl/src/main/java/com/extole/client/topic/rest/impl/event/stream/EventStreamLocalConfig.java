package com.extole.client.topic.rest.impl.event.stream;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.extole.common.rest.client.JerseyClientConfigBuilder;
import com.extole.common.rest.util.DelayedHttpRequestRetryHandler;

@Configuration
public class EventStreamLocalConfig {

    @Bean(name = "eventStreamApiClient")
    public Client getEventStreamApiClient(EventStreamLocalApiConnectionProperties connectionProperties) {
        HttpRequestRetryHandler retryHandler = createHttpRequestRetryHandler(
            connectionProperties.getRetryMaxAttempts(), connectionProperties.getRetryDelay());

        return JerseyClientConfigBuilder.newConfig()
            .withMaxTotal(connectionProperties.getConnectionPoolSize())
            .withDefaultMaxPerRoute(connectionProperties.getConnectionPoolSize())
            .withConnectionTimeout(connectionProperties.getConnectionTimeout())
            .withConnectionTtl(connectionProperties.getConnectionTtl())
            .withReadTimeout(connectionProperties.getReadTimeout())
            .withRetryHandler(retryHandler)
            .build();
    }

    private HttpRequestRetryHandler createHttpRequestRetryHandler(int retryMaxAttempts, long retryDelay) {
        return new DelayedHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(retryMaxAttempts, false),
            retryDelay);
    }
}
