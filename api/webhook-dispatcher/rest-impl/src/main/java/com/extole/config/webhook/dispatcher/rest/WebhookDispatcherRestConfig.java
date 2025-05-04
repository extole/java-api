package com.extole.config.webhook.dispatcher.rest;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.extole.common.rest.client.JerseyClientConfigBuilder;
import com.extole.common.rest.util.DelayedHttpRequestRetryHandler;
import com.extole.config.common.rest.support.authorization.client.CommonRestClientAuthorizationSupportConfig;
import com.extole.config.webhook.dispatch.result.event.WebhookDispatchResultEventServiceConfigMarker;
import com.extole.config.webhook.event.WebhookEventServiceConfigMarker;
import com.extole.webhook.dispatcher.rest.impl.WebhookDispatcherApiConnectionProperties;
import com.extole.webhook.dispatcher.rest.impl.WebhookDispatcherRestPackageMarker;

@Configuration
@ComponentScan(
    basePackageClasses = {
        WebhookDispatcherRestPackageMarker.class,
        WebhookEventServiceConfigMarker.class,
        WebhookDispatchResultEventServiceConfigMarker.class,
        CommonRestClientAuthorizationSupportConfig.class
    })
public class WebhookDispatcherRestConfig {

    @Bean(name = "webhookDispatcherApiClient")
    public Client getWebhookDispatcherApiClient(WebhookDispatcherApiConnectionProperties connectionProperties) {
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
