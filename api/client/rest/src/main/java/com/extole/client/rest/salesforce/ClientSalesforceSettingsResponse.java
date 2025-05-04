package com.extole.client.rest.salesforce;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientSalesforceSettingsResponse {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_BASE_URI = "base_uri";
    private static final String JSON_SITE_ID = "site_id";
    private static final String JSON_USER_NAME = "user_name";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_DISABLED = "disabled";

    private final String settingsId;
    private final String settingsName;
    private final String baseUri;
    private final String siteId;
    private final String username;
    private final String password;
    private final Boolean disabled;

    @JsonCreator
    public ClientSalesforceSettingsResponse(@JsonProperty(JSON_ID) String settingsId,
        @JsonProperty(JSON_NAME) String settingsName,
        @JsonProperty(JSON_BASE_URI) String baseUri,
        @JsonProperty(JSON_SITE_ID) String siteId,
        @JsonProperty(JSON_USER_NAME) String username,
        @JsonProperty(JSON_PASSWORD) String password,
        @JsonProperty(JSON_DISABLED) Boolean disabled) {
        this.settingsId = settingsId;
        this.settingsName = settingsName;
        this.baseUri = baseUri;
        this.siteId = siteId;
        this.username = username;
        this.password = password;
        this.disabled = disabled;
    }

    @JsonProperty(JSON_ID)
    public String getSettingsId() {
        return settingsId;
    }

    @JsonProperty(JSON_NAME)
    public String getSettingsName() {
        return settingsName;
    }

    @JsonProperty(JSON_BASE_URI)
    public String getBaseUri() {
        return baseUri;
    }

    @JsonProperty(JSON_SITE_ID)
    public String getSiteId() {
        return siteId;
    }

    @JsonProperty(JSON_USER_NAME)
    public String getUsername() {
        return username;
    }

    @JsonProperty(JSON_PASSWORD)
    public String getPassword() {
        return password;
    }

    @JsonProperty(JSON_DISABLED)
    public Boolean getDisabled() {
        return disabled;
    }

}
