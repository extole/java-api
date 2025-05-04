package com.extole.config.reporting.rest;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.extole.common.rest.client.JerseyClientConfigBuilder;
import com.extole.common.rest.util.DelayedHttpRequestRetryHandler;
import com.extole.config.audience.membership.AudienceMembershipBootstrapConfig;
import com.extole.config.common.rest.support.authorization.client.CommonRestClientAuthorizationSupportConfig;
import com.extole.config.common.rest.support.authorization.person.CommonRestPersonAuthorizationSupportConfig;
import com.extole.model.service.config.ModelBootstrapSpringConfig;
import com.extole.reporting.rest.impl.ReportingRestConfigMarker;
import com.extole.reporting.rest.impl.batch.BatchJobApiConnectionProperties;

@Configuration
@ComponentScan(
    basePackageClasses = {
        AudienceMembershipBootstrapConfig.class,
        ReportingRestConfigMarker.class,
        ModelBootstrapSpringConfig.class,
        CommonRestClientAuthorizationSupportConfig.class,
        CommonRestPersonAuthorizationSupportConfig.class
    })
public class ReportingRestConfig {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "batchJobApiClient")
    public Client getBatchJobApiClient(BatchJobApiConnectionProperties connectionProperties) {
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
