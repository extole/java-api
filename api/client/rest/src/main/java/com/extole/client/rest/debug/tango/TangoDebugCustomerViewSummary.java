package com.extole.client.rest.debug.tango;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TangoDebugCustomerViewSummary {
    private static final String JSON_PROPERTY_CUSTOMER_IDENTIFIER = "customer_identifier";
    private static final String JSON_PROPERTY_DISPLAY_NAME = "display_name";
    private static final String JSON_PROPERTY_STATUS = "status";
    private static final String JSON_PROPERTY_CREATED_AT = "created_at";
    private static final String JSON_PROPERTY_ACCOUNTS = "accounts";

    private final String customerIdentifier;
    private final String displayName;
    private final String status;
    private final String createdAt;
    private final List<TangoDebugAccountViewSummary> accounts;

    public TangoDebugCustomerViewSummary(@JsonProperty(JSON_PROPERTY_CUSTOMER_IDENTIFIER) String customerIdentifier,
        @JsonProperty(JSON_PROPERTY_DISPLAY_NAME) String displayName, @JsonProperty(JSON_PROPERTY_STATUS) String status,
        @JsonProperty(JSON_PROPERTY_CREATED_AT) String createdAt,
        @JsonProperty(JSON_PROPERTY_ACCOUNTS) List<TangoDebugAccountViewSummary> accounts) {
        this.customerIdentifier = customerIdentifier;
        this.displayName = displayName;
        this.status = status;
        this.createdAt = createdAt;
        this.accounts = accounts;
    }

    @JsonProperty(JSON_PROPERTY_CUSTOMER_IDENTIFIER)
    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    public String getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_CREATED_AT)
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(JSON_PROPERTY_ACCOUNTS)
    public List<TangoDebugAccountViewSummary> getAccounts() {
        return accounts;
    }
}
