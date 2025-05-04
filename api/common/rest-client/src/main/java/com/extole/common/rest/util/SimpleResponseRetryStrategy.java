package com.extole.common.rest.util;

import java.util.Set;
import java.util.function.Predicate;

import javax.ws.rs.core.Response.Status.Family;

import com.google.common.collect.ImmutableSet;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleResponseRetryStrategy implements ServiceUnavailableRetryStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleResponseRetryStrategy.class);
    private static final String UNKNOWN_REQUEST_LINE = "unknown";

    private static final RetryNotificationListener NO_OP_NOTIFICATION_LISTENER = new NoOpRetryNotificationListener();
    private static final Set<Family> ERROR_FAMILY_SET = ImmutableSet.of(Family.SERVER_ERROR, Family.CLIENT_ERROR);

    public static final Predicate<StatusLine> ERROR_FAMILY_RETRY_PREDICATE =
        statusLine -> ERROR_FAMILY_SET.contains(Family.familyOf(statusLine.getStatusCode()));
    public static final Predicate<StatusLine> SERVICE_UNAVAILABLE_RETRY_PREDICATE =
        statusLine -> statusLine.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE;

    private final int retryCount;
    private final long retryIntervalMillis;
    private final Predicate<StatusLine> retryPredicate;
    private final RetryNotificationListener notificationListener;

    public SimpleResponseRetryStrategy(int retryCount, long retryIntervalMillis, Predicate<StatusLine> retryPredicate) {
        this(retryCount, retryIntervalMillis, retryPredicate, NO_OP_NOTIFICATION_LISTENER);
    }

    public SimpleResponseRetryStrategy(int retryCount, long retryIntervalMillis, Predicate<StatusLine> retryPredicate,
        RetryNotificationListener notificationListener) {
        this.retryCount = retryCount;
        this.retryIntervalMillis = retryIntervalMillis;
        this.retryPredicate = retryPredicate;
        this.notificationListener = notificationListener;
    }

    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        StatusLine statusLine = response.getStatusLine();
        boolean retryRequest = executionCount <= retryCount && retryPredicate.test(statusLine);
        String requestLine = getRequestLine(context);
        if (retryRequest) {
            LOG.warn("Received response: {} for request: {}, executionCount={}. Retrying in {}(ms)", response,
                requestLine, Integer.valueOf(executionCount), Long.valueOf(retryIntervalMillis));
            notificationListener.onRetry(statusLine);
        } else if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            LOG.warn("Received response: {} for request: {}, executionCount={}. Not going to retry.", response,
                requestLine, Integer.valueOf(executionCount));
        }
        return retryRequest;
    }

    @Override
    public long getRetryInterval() {
        return retryIntervalMillis;
    }

    private static String getRequestLine(HttpContext context) {
        HttpRequest httpRequest = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
        HttpHost httpHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);

        if (httpRequest != null) {
            RequestLine requestLine = httpRequest.getRequestLine();
            if (httpHost != null) {
                return requestLine.getMethod() + " "
                    + httpHost.toURI() + requestLine.getUri() + " "
                    + requestLine.getProtocolVersion();
            }
            return requestLine.toString();
        }
        if (httpHost != null) {
            return httpHost.toURI();
        }
        return UNKNOWN_REQUEST_LINE;
    }
}
