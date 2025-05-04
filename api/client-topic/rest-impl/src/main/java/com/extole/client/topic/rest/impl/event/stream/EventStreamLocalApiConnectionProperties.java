package com.extole.client.topic.rest.impl.event.stream;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.common.lang.ToString;

@Component
public final class EventStreamLocalApiConnectionProperties {

    private final int connectionTimeout;
    private final int readTimeout;
    private final int connectionPoolSize;
    private final Duration connectionTtl;
    private final int retryMaxAttempts;
    private final long retryDelay;

    @Autowired
    EventStreamLocalApiConnectionProperties(
        @Value("${event.stream.view.client.http.connectionTimeout:10000}") int connectionTimeout,
        @Value("${event.stream.view.client.http.readTimeout:90000}") int readTimeout,
        @Value("${event.stream.view.client.http.connectionPoolSize:20}") int connectionPoolSize,
        @Value("${event.stream.view.client.http.connectionTtl:5000}") int connectionTtl,
        @Value("${event.stream.view.client.http.retryMaxAttempts:3}") int retryMaxAttempts,
        @Value("${event.stream.view.client.http.retryDelay:2000}") long retryDelay) {
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

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
