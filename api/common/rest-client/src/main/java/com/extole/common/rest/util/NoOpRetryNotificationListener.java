package com.extole.common.rest.util;

import java.io.IOException;

import org.apache.http.StatusLine;

public class NoOpRetryNotificationListener implements RetryNotificationListener {

    @Override
    public void onRetry(StatusLine statusLine) {
        // empty
    }

    @Override
    public void onRetry(IOException exception) {
        // empty
    }

}
