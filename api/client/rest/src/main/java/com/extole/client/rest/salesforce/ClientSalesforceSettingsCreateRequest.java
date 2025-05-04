package com.extole.client.rest.salesforce;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientSalesforceSettingsCreateRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_BASE_URI = "base_uri";
    private static final String JSON_SITE_ID = "site_id";
    private static final String JSON_USER_NAME = "user_name";
    private static final String JSON_PASSWORD = "password";

    private final String name;
    private final String baseUri;
    private final String siteId;
    private final String username;
    private final String password;

    @JsonCreator
    public ClientSalesforceSettingsCreateRequest(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_BASE_URI) String baseUri,
        @JsonProperty(JSON_SITE_ID) String siteId,
        @JsonProperty(JSON_USER_NAME) String username,
        @JsonProperty(JSON_PASSWORD) String password) {
        this.name = name;
        this.baseUri = baseUri;
        this.siteId = siteId;
        this.username = username;
        this.password = password;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
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

}
