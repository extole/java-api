package com.extole.config.topic.rest;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.client.topic.rest.impl.notification.ClientRestApiConnectionProperties;
import com.extole.client.topic.rest.impl.notification.ClientTopicRestPackageMarker;
import com.extole.common.rest.client.JerseyClientConfigBuilder;
import com.extole.common.rest.util.DelayedHttpRequestRetryHandler;
import com.extole.config.notification.event.NotificationEventServiceConfigMarker;

@Configuration
@ComponentScan(
    basePackageClasses = {
        ClientTopicRestPackageMarker.class,
        NotificationEventServiceConfigMarker.class
    })
public class ClientTopicRestConfig {

    @Bean(name = "notificationApiClient")
    public Client getNotificationApiClient(ClientRestApiConnectionProperties connectionProperties) {
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
