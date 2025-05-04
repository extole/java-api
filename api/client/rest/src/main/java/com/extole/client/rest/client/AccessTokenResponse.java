package com.extole.client.rest.client;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

    public enum Type {
        USER, MANAGED, RESOURCE
    }

    private static final String JSON_TYPE = "type";
    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_IDENTITY_ID = "identity_id";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_EXPIRES_ID = "expires_in";
    private static final String JSON_SCOPES = "scopes";

    private final Type type;
    private final String token;
    private final String clientId;
    private final String identityId;
    private final long expiresIn;
    private final Set<Scope> scopes;

    @JsonCreator
    AccessTokenResponse(
        @JsonProperty(JSON_TYPE) Type type,
        @JsonProperty(JSON_ACCESS_TOKEN) String token,
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_IDENTITY_ID) String identityId,
        @JsonProperty(JSON_EXPIRES_ID) long expiresIn,
        @JsonProperty(JSON_SCOPES) Set<Scope> scopes) {
        this.type = type;
        this.token = token;
        this.clientId = clientId;
        this.identityId = identityId;
        this.expiresIn = expiresIn;
        this.scopes = scopes;
    }

    @JsonProperty
    public Type getType() {
        return type;
    }

    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getToken() {
        return token;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_IDENTITY_ID)
    public String getIdentityId() {
        return identityId;
    }

    @Deprecated // TODO remove as a later part of ENG-21237
    @JsonProperty(JSON_PERSON_ID)
    public String getPersonId() {
        return identityId;
    }

    @JsonProperty(JSON_EXPIRES_ID)
    public long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty(JSON_SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Type type;
        private String token;
        private String clientId;
        private String identityId;
        private long expiresIn;
        private Set<Scope> scopes;

        private Builder() {
        }

        public Builder withType(Type type) {
            this.type = type;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withIdentityId(String identityId) {
            this.identityId = identityId;
            return this;
        }

        public Builder withExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public AccessTokenResponse build() {
            return new AccessTokenResponse(type, token, clientId, identityId, expiresIn, scopes);
        }
    }
}
