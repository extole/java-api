package com.extole.client.rest.auth.provider.type.openid.connect;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.extole.client.rest.client.Scope;
import com.extole.common.lang.ToString;

public class OpenIdConnectAuthProviderTypeCreateRequest {

    private static final String NAME = "name";
    private static final String DOMAIN = "domain";
    private static final String APPLICATION_ID = "application_id";
    private static final String APPLICATION_SECRET = "application_secret";
    private static final String CUSTOM_PARAMS = "custom_params";
    private static final String SCOPES = "scopes";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";

    private final String name;
    private final String domain;
    private final String applicationId;
    private final byte[] applicationSecret;
    private final Map<String, String> customParams;
    private final Set<Scope> scopes;
    private final Category category;
    private final String description;

    public OpenIdConnectAuthProviderTypeCreateRequest(@JsonProperty(NAME) String name,
        @JsonProperty(DOMAIN) String domain,
        @JsonProperty(APPLICATION_ID) String applicationId,
        @JsonProperty(APPLICATION_SECRET) String applicationSecret,
        @Nullable @JsonProperty(CUSTOM_PARAMS) Map<String, String> customParams,
        @JsonProperty(SCOPES) Set<Scope> scopes,
        @JsonProperty(CATEGORY) Category category,
        @Nullable @JsonProperty(DESCRIPTION) String description) {
        this.name = name;
        this.domain = domain;
        this.applicationId = applicationId;
        this.applicationSecret =
            applicationSecret == null ? null : applicationSecret.getBytes(StandardCharsets.ISO_8859_1);
        this.customParams = customParams;
        this.scopes = scopes;
        this.category = category;
        this.description = description;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(DOMAIN)
    public String getDomain() {
        return domain;
    }

    @JsonProperty(APPLICATION_ID)
    public String getApplicationId() {
        return applicationId;
    }

    @JsonProperty(APPLICATION_SECRET)
    public String getApplicationSecret() {
        return applicationSecret == null ? null : new String(applicationSecret, StandardCharsets.ISO_8859_1);
    }

    @Nullable
    @JsonProperty(CUSTOM_PARAMS)
    public Map<String, String> getCustomParams() {
        return customParams;
    }

    @JsonProperty(SCOPES)
    public Set<Scope> getScopes() {
        return scopes == null ? Sets.newHashSet() : scopes;
    }

    @Nullable
    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(CATEGORY)
    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;
        private String domain;
        private String applicationId;
        private String applicationSecret;
        private Map<String, String> customParams;
        private Set<Scope> scopes;
        private Category category;
        private String description;

        private Builder() {

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder withApplicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder withApplicationSecret(String applicationSecret) {
            this.applicationSecret = applicationSecret;
            return this;
        }

        public Builder withCustomParams(Map<String, String> customParams) {
            this.customParams = customParams;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public OpenIdConnectAuthProviderTypeCreateRequest build() {
            return new OpenIdConnectAuthProviderTypeCreateRequest(name,
                domain,
                applicationId,
                applicationSecret,
                customParams,
                scopes,
                category,
                description);
        }
    }

}
