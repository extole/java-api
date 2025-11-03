package com.extole.common.javascript;

import java.util.List;

public class JavascriptExecutionTimeoutException extends JavascriptExecutionException {

    public JavascriptExecutionTimeoutException(String message) {
        super(message);
    }

    public JavascriptExecutionTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavascriptExecutionTimeoutException(String message, Throwable cause, List<String> output) {
        super(message, cause, output);
    }
}
