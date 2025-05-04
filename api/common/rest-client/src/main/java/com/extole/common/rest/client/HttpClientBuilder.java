package com.extole.common.rest.client;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import com.extole.common.rest.util.DelayedHttpRequestRetryHandler;
import com.extole.common.rest.util.SimpleHttpRequestRetryHandler;
import com.extole.common.rest.util.SimpleResponseRetryStrategy;

public class HttpClientBuilder {

    private static final int CONNECT_TIMEOUT_MS_DEFAULT = 3000;
    private static final int SOCKET_TIMEOUT_MS_DEFAULT = 60000;
    private static final int MAX_CONNECTIONS_DEFAULT = 100;
    private static final int MAX_CONNECTIONS_PER_ROUTE_DEFAULT = 50;
    private static final int MAX_REDIRECTS_DEFAULT = 10;
    private static final int RETRY_COUNT_DEFAULT = 3;
    private static final int RETRY_INTERVAL_MS_DEFAULT = 100;
    private static final int CONNECTION_TTL_MS_DEFAULT = 5000;

    private Predicate<StatusLine> retryPredicate = SimpleResponseRetryStrategy.ERROR_FAMILY_RETRY_PREDICATE;
    private int connectTimeoutMs = CONNECT_TIMEOUT_MS_DEFAULT;
    private int socketTimeoutMs = SOCKET_TIMEOUT_MS_DEFAULT;
    private int maxConnections = MAX_CONNECTIONS_DEFAULT;
    private int maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE_DEFAULT;
    private boolean redirectsEnabled = false;
    private int maxRedirects = MAX_REDIRECTS_DEFAULT;
    private int retryCount = RETRY_COUNT_DEFAULT;
    private int retryIntervalMs = RETRY_INTERVAL_MS_DEFAULT;
    private int connectionTtlMs = CONNECTION_TTL_MS_DEFAULT;
    private String cookieSpec = CookieSpecs.DEFAULT;
    private Optional<String> userAgent = Optional.of("Extole/2.0");
    private Optional<String> appName = Optional.empty();
    private Optional<CookieStore> cookieStore = Optional.empty();
    private boolean requestSentRetryEnabled = false;

    public HttpClientBuilder withConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        return this;
    }

    public HttpClientBuilder withSocketTimeoutMs(int socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
        return this;
    }

    public HttpClientBuilder withMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;

    }

    public HttpClientBuilder withMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        return this;
    }

    public HttpClientBuilder withRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
        return this;
    }

    public HttpClientBuilder withRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public HttpClientBuilder withRetryIntervalMs(int retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
        return this;
    }

    public HttpClientBuilder withRetryPredicate(Predicate<StatusLine> retryPredicate) {
        this.retryPredicate = retryPredicate;
        return this;
    }

    public HttpClientBuilder withMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
        return this;
    }

    public HttpClientBuilder withConnectionTtlMs(int connectionTtlMs) {
        this.connectionTtlMs = connectionTtlMs;
        return this;
    }

    public HttpClientBuilder withCookieSpec(String cookieSpec) {
        this.cookieSpec = cookieSpec;
        return this;
    }

    public HttpClientBuilder withUserAgent(String userAgent) {
        this.userAgent = Optional.ofNullable(userAgent);
        return this;
    }

    public HttpClientBuilder withAppName(String appName) {
        this.appName = Optional.ofNullable(appName);
        return this;
    }

    public HttpClientBuilder clearUserAgent() {
        this.userAgent = Optional.empty();
        return this;
    }

    public HttpClientBuilder withCookieStore(CookieStore cookieStore) {
        this.cookieStore = Optional.of(cookieStore);
        return this;
    }

    public HttpClientBuilder withRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
        return this;
    }

    public CloseableHttpClient build() {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(connectTimeoutMs)
            .setSocketTimeout(socketTimeoutMs)
            .setRedirectsEnabled(redirectsEnabled)
            .setCookieSpec(cookieSpec)
            .setMaxRedirects(maxRedirects)
            .build();

        SimpleHttpRequestRetryHandler simpleRetryHandler = SimpleHttpRequestRetryHandler.newBuilder()
            .withRetryCount(retryCount)
            .withRequestSentRetryEnabled(requestSentRetryEnabled)
            .build();
        HttpRequestRetryHandler retryHandler = new DelayedHttpRequestRetryHandler(simpleRetryHandler, retryIntervalMs);
        ServiceUnavailableRetryStrategy retryStrategy = new SimpleResponseRetryStrategy(retryCount, retryIntervalMs,
            retryPredicate);

        org.apache.http.impl.client.HttpClientBuilder httpClientBuilder =
            org.apache.http.impl.client.HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(retryHandler)
                .setServiceUnavailableRetryStrategy(retryStrategy)
                .setMaxConnTotal(maxConnections)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .setConnectionTimeToLive(connectionTtlMs, TimeUnit.MILLISECONDS);

        userAgent.ifPresent(agent -> {
            String userAgentName = appName.map(app -> agent + " " + app).orElse(agent);
            httpClientBuilder.addInterceptorLast(new DefaultUserAgentHttpRequestInterceptor(userAgentName));
        });

        if (userAgent.isPresent()) {
            httpClientBuilder.disableDefaultUserAgent();
        }

        cookieStore.ifPresent(value -> httpClientBuilder.setDefaultCookieStore(value));

        return httpClientBuilder.build();
    }

    private static final class DefaultUserAgentHttpRequestInterceptor implements HttpRequestInterceptor {

        private final String defaultUserAgent;

        private DefaultUserAgentHttpRequestInterceptor(String defaultUserAgent) {
            this.defaultUserAgent = defaultUserAgent;
        }

        @Override
        public void process(HttpRequest request, HttpContext context) {
            if (request.getHeaders(HttpHeaders.USER_AGENT).length == 0) {
                request.addHeader(HttpHeaders.USER_AGENT, defaultUserAgent);
            }
        }

    }

}
