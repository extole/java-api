package com.extole.consumer.rest.impl.request.context;

public class JwtAuthorizationExtractionException extends Exception {

    private static final String MESSAGE_PATTERN =
        "Could not extract authorization from jwt. Reason: %s. Description: %s";

    private final String reason;
    private final String description;

    public JwtAuthorizationExtractionException(String reason, String description) {
        super(String.format(MESSAGE_PATTERN, reason, description));
        this.reason = reason;
        this.description = description;
    }

    public JwtAuthorizationExtractionException(String reason, String description, Throwable cause) {
        super(String.format(MESSAGE_PATTERN, reason, description), cause);
        this.reason = reason;
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }
}
