package com.extole.consumer.rest.impl.request.context;

public class RequestBodySizeTooLargeException extends Exception {

    private final int bodySize;
    private final int maxAllowedBodySize;

    public RequestBodySizeTooLargeException(String message, int bodySize, int maxAllowedBodySize) {
        super(String.format("%s. Body size: %s, max allowed body size: %s.", message, bodySize, maxAllowedBodySize));
        this.bodySize = bodySize;
        this.maxAllowedBodySize = maxAllowedBodySize;
    }

    public int getBodySize() {
        return bodySize;
    }

    public int getMaxAllowedBodySize() {
        return maxAllowedBodySize;
    }
}
