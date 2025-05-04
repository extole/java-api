package com.extole.common.rest.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

public final class SimpleHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {

    private SimpleHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled,
        Collection<Class<? extends IOException>> nonRetriableExceptionClasses) {
        super(retryCount, requestSentRetryEnabled, nonRetriableExceptionClasses);
    }

    public static SimpleHttpRequestRetryHandler.Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private static final int DEFAULT_RETRY_COUNT = 3;

        private int retryCount = DEFAULT_RETRY_COUNT;
        private boolean requestSentRetryEnabled = false;
        private Collection<Class<? extends IOException>> nonRetriableExceptionClasses = Collections.emptyList();

        private Builder() {
        }

        public Builder withRetryCount(int retryCount) {
            if (retryCount < 0) {
                throw new IllegalArgumentException("Cannot have negative retry count");
            }
            this.retryCount = retryCount;
            return this;
        }

        public Builder withRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
            this.requestSentRetryEnabled = requestSentRetryEnabled;
            return this;
        }

        public Builder withNonRetriableExceptionClasses(Collection<Class<? extends IOException>> classes) {
            this.nonRetriableExceptionClasses = classes;
            return this;
        }

        public SimpleHttpRequestRetryHandler build() {
            return new SimpleHttpRequestRetryHandler(retryCount, requestSentRetryEnabled, nonRetriableExceptionClasses);
        }
    }
}
