package com.extole.consumer.rest.authorization;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.consumer.rest.common.Scope;

@JsonInclude(Include.NON_NULL)
public class TokenResponse {

    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String SCOPES = "scopes";
    private final String token;
    private final long expiresIn;
    private final Set<Scope> scopes;

    public TokenResponse(@JsonProperty(ACCESS_TOKEN) String token,
        @JsonProperty(EXPIRES_IN) long expiresIn,
        @JsonProperty(SCOPES) Set<Scope> scopes) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.scopes = ImmutableSet.copyOf(scopes);
    }

    @JsonProperty(ACCESS_TOKEN)
    public String getToken() {
        return token;
    }

    @JsonProperty(EXPIRES_IN)
    public long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty(SCOPES)
    public Set<Scope> getScopes() {
        return scopes;
    }

}
