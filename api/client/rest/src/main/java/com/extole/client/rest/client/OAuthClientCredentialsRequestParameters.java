package com.extole.client.rest.client;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.extole.common.lang.ToString;

public class OAuthClientCredentialsRequestParameters {

    private final String grantType;
    private final Optional<String> clientSecret;
    private final Optional<String> clientId;
    private final Optional<String> scope;

    @JsonCreator
    public OAuthClientCredentialsRequestParameters(@FormParam("grant_type") String grantType,
        @FormParam("client_secret") Optional<String> clientSecret,
        @FormParam("client_id") Optional<String> clientId,
        @FormParam("scope") Optional<String> scope) {
        this.grantType = grantType;
        this.clientSecret = clientSecret;
        this.clientId = clientId;
        this.scope = scope;
    }

    @FormParam("grant_type")
    public String getGrantType() {
        return grantType;
    }

    @FormParam("client_secret")
    public Optional<String> getClientSecret() {
        return clientSecret;
    }

    @FormParam("client_id")
    public Optional<String> getClientId() {
        return clientId;
    }

    @Nullable
    @FormParam("scope")
    public Optional<String> getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String grantType;
        private Optional<String> clientSecret = Optional.empty();
        private Optional<String> clientId = Optional.empty();
        private Optional<String> scope = Optional.empty();

        private Builder() {
        }

        public Builder withGrantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder withClientSecret(String clientSecret) {
            this.clientSecret = Optional.ofNullable(clientSecret);
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = Optional.ofNullable(clientId);
            return this;
        }

        public Builder withScope(String scope) {
            this.scope = Optional.ofNullable(scope);
            return this;
        }

        public OAuthClientCredentialsRequestParameters build() {
            return new OAuthClientCredentialsRequestParameters(grantType, clientSecret, clientId, scope);
        }
    }
}
