package com.extole.consumer.rest.impl.report.restclient;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ReportApiProperties {

    private final String url;
    private final int connectionTimeout;
    private final int readTimeout;
    private final int connectionPoolSize;
    private final Duration connectionTtl;

    public ReportApiProperties(
        @Value("${extole.environment:lo}") String environment,
        @Value("${report.url:https://api%s.extole.io}") String url,
        @Value("${report.http.connectionTimeout:60000}") int connectionTimeout,
        @Value("${report.http.connectionTtl:5000}") int connectionTtl,
        @Value("${report.http.readTimeout:60000}") int readTimeout,
        @Value("${report.http.connectionPoolSize:20}") int connectionPoolSize) {
        this.url = String.format(url, getEnvironmentUriPrefix(environment));
        this.connectionTimeout = connectionTimeout;
        this.connectionTtl = Duration.ofMillis(connectionTtl);
        this.readTimeout = readTimeout;
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getUrl() {
        return url;
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

    private String getEnvironmentUriPrefix(String environment) {
        switch (environment) {
            case "pr":
                return "";
            default:
                return "." + environment;
        }
    }
}
