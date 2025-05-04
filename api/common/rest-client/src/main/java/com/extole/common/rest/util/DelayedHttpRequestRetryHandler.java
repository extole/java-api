package com.extole.common.rest.util;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedHttpRequestRetryHandler implements HttpRequestRetryHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DelayedHttpRequestRetryHandler.class);
    private static final String UNKNOWN_REQUEST_LINE = "unknown";
    private static final RetryNotificationListener NO_OP_NOTIFICATION_LISTENER = new NoOpRetryNotificationListener();

    private final HttpRequestRetryHandler delegate;
    private final long delayMillis;
    private final RetryNotificationListener notificationListener;

    public DelayedHttpRequestRetryHandler(HttpRequestRetryHandler delegate, long delayMillis) {
        this(delegate, delayMillis, NO_OP_NOTIFICATION_LISTENER);
    }

    public DelayedHttpRequestRetryHandler(HttpRequestRetryHandler delegate, long delayMillis,
        RetryNotificationListener notificationListener) {
        this.delegate = delegate;
        this.delayMillis = delayMillis;
        this.notificationListener = notificationListener;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        String requestLine = getRequestLine(context);

        if (Thread.currentThread().isInterrupted()) {
            LOG.warn("Error executing: {}, executionCount={}. Not retrying because the current thread is interrupted",
                requestLine, Integer.valueOf(executionCount), exception);
            return false;
        }

        boolean retryRequest = delegate.retryRequest(exception, executionCount, context);

        if (retryRequest) {
            notificationListener.onRetry(exception);
            if (delayMillis > 0) {
                LOG.debug("Error executing: {}, executionCount={}. Retrying in {}(ms)", requestLine,
                    Integer.valueOf(executionCount), Long.valueOf(delayMillis), exception);
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOG.warn("Thread interrupted while sleeping {}(ms) before retrying: {}, executionCount={}",
                        Long.valueOf(delayMillis), requestLine, Integer.valueOf(executionCount), e);
                    return false;
                }
            } else {
                LOG.debug("Error executing: {}, executionCount={}. Retrying immediately.", requestLine,
                    Integer.valueOf(executionCount), Long.valueOf(delayMillis), exception);
            }
        } else {
            LOG.warn("Error executing: {}, executionCount={}. Not going to retry.", requestLine,
                Integer.valueOf(executionCount), exception);
        }

        return retryRequest;
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
