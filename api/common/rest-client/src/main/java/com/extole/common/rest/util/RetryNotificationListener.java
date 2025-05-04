package com.extole.common.rest.util;

import java.io.IOException;

import org.apache.http.StatusLine;

public interface RetryNotificationListener {

    void onRetry(StatusLine statusLine);

    void onRetry(IOException exception);
}
