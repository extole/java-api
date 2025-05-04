package com.extole.client.rest.salesforce;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientSalesforceSettingsUpdateRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_BASE_URI = "base_uri";
    private static final String JSON_SITE_ID = "site_id";
    private static final String JSON_USER_NAME = "user_name";
    private static final String JSON_PASSWORD = "password";
    private static final String JSON_DISABLED = "disabled";

    private final String name;
    private final String baseUri;
    private final String siteId;
    private final String username;
    private final String password;
    private final Boolean disabled;

    @JsonCreator
    public ClientSalesforceSettingsUpdateRequest(
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_BASE_URI) String baseUri,
        @Nullable @JsonProperty(JSON_SITE_ID) String siteId,
        @Nullable @JsonProperty(JSON_USER_NAME) String username,
        @Nullable @JsonProperty(JSON_PASSWORD) String password,
        @Nullable @JsonProperty(JSON_DISABLED) Boolean disabled) {
        this.name = name;
        this.baseUri = baseUri;
        this.siteId = siteId;
        this.username = username;
        this.password = password;
        this.disabled = disabled;
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

    @JsonProperty(JSON_DISABLED)
    public Boolean getDisabled() {
        return disabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String settingsName;
        private String baseUri;
        private String siteId;
        private String username;
        private String password;
        private Boolean disabled;

        private Builder() {
        }

        public Builder withSettingsName(String settingsName) {
            this.settingsName = settingsName;
            return this;
        }

        public Builder withBaseUri(String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public Builder withSiteId(String siteId) {
            this.siteId = siteId;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withDisabled(Boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public ClientSalesforceSettingsUpdateRequest build() {
            return new ClientSalesforceSettingsUpdateRequest(settingsName, baseUri, siteId, username, password,
                disabled);
        }
    }
}
