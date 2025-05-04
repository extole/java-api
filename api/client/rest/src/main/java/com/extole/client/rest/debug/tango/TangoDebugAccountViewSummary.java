package com.extole.client.rest.debug.tango;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoDebugAccountViewSummary {

    private static final String JSON_PROPERTY_ACCOUNT_IDENTIFIER = "account_dentifier";
    private static final String JSON_PROPERTY_CREATED_AT = "created_at";
    private static final String JSON_PROPERTY_DISPLAY_NAME = "display_name";
    private static final String JSON_PROPERTY_STATUS = "status";

    private final String accountIdentifier;
    private final String createdAt;
    private final String displayName;
    private final String status;

    @JsonCreator
    public TangoDebugAccountViewSummary(@JsonProperty(JSON_PROPERTY_ACCOUNT_IDENTIFIER) String accountIdentifier,
        @JsonProperty(JSON_PROPERTY_CREATED_AT) String createdAt,
        @JsonProperty(JSON_PROPERTY_DISPLAY_NAME) String displayName,
        @JsonProperty(JSON_PROPERTY_STATUS) String status) {
        this.accountIdentifier = accountIdentifier;
        this.createdAt = createdAt;
        this.displayName = displayName;
        this.status = status;
    }

    @JsonProperty(JSON_PROPERTY_ACCOUNT_IDENTIFIER)
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    @JsonProperty(JSON_PROPERTY_CREATED_AT)
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    public String getStatus() {
        return status;
    }

}
