package com.extole.client.topic.rest.impl.notification;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ClientRestApiConnectionProperties {

    private final int connectionTimeout;
    private final int readTimeout;
    private final int connectionPoolSize;
    private final Duration connectionTtl;
    private final int retryMaxAttempts;
    private final long retryDelay;

    ClientRestApiConnectionProperties(
        @Value("${notification.client.http.connectionTimeout:10000}") int connectionTimeout,
        @Value("${notification.client.http.readTimeout:90000}") int readTimeout,
        @Value("${notification.client.http.connectionPoolSize:20}") int connectionPoolSize,
        @Value("${notification.client.http.connectionTtl:5000}") int connectionTtl,
        @Value("${notification.client.http.retryMaxAttempts:3}") int retryMaxAttempts,
        @Value("${notification.client.http.retryDelay:2000}") long retryDelay) {

        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.connectionPoolSize = connectionPoolSize;
        this.connectionTtl = Duration.ofMillis(connectionTtl);
        this.retryMaxAttempts = retryMaxAttempts;
        this.retryDelay = retryDelay;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public Duration getConnectionTtl() {
        return connectionTtl;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

}
