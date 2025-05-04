package com.extole.client.rest.client;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.id.Id;

public class ResourceAccessTokenResponse {

    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_IDENTITY_ID = "identity_id";
    private static final String JSON_EXPIRES_ID = "expires_in";
    private static final String JSON_RESOURCES = "resources";
    private final String token;
    private final Id<?> identityId;
    private final long expiresIn;
    private final List<AccessTokenResourceResponse> resources;

    @JsonCreator
    public ResourceAccessTokenResponse(@JsonProperty(JSON_ACCESS_TOKEN) String token,
        @JsonProperty(JSON_IDENTITY_ID) Id<?> identityId,
        @JsonProperty(JSON_EXPIRES_ID) long expiresIn,
        @JsonProperty(JSON_RESOURCES) List<AccessTokenResourceResponse> resources) {
        this.token = token;
        this.identityId = identityId;
        this.expiresIn = expiresIn;
        this.resources = ImmutableList.copyOf(resources);
    }

    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getToken() {
        return token;
    }

    @JsonProperty(JSON_IDENTITY_ID)
    public Id<?> getIdentityId() {
        return identityId;
    }

    @JsonProperty(JSON_EXPIRES_ID)
    public long getExpiresIn() {
        return expiresIn;
    }

    public List<AccessTokenResourceResponse> getResources() {
        return resources;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String token;
        private Id<?> identityId;
        private long expiresIn;
        private List<AccessTokenResourceResponse> resources = new ArrayList<>();

        private Builder() {
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withIdentityId(Id<?> identityId) {
            this.identityId = identityId;
            return this;
        }

        public Builder withExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder withResources(List<AccessTokenResourceResponse> resources) {
            this.resources = resources;
            return this;
        }

        public ResourceAccessTokenResponse build() {
            return new ResourceAccessTokenResponse(token, identityId, expiresIn, resources);
        }
    }
}
