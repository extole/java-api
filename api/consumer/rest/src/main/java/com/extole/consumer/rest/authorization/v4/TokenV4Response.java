package com.extole.consumer.rest.authorization.v4;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.consumer.rest.common.Scope;

@JsonInclude(Include.NON_NULL)
public class TokenV4Response {
    private final String token;
    private final long expiresIn;
    private final Set<Scope> scopes;
    private final Set<Scope> capabilities;

    public TokenV4Response(@JsonProperty("access_token") String token,
        @JsonProperty("expires_in") long expiresIn, @JsonProperty("scopes") Set<Scope> scopes,
        @JsonProperty("capabilities") Set<Scope> capabilities) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.scopes = scopes;
        this.capabilities = capabilities;
    }

    @JsonProperty("access_token")
    public String getToken() {
        return token;
    }

    @JsonProperty("expires_in")
    public long getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("scopes")
    public Set<Scope> getScopes() {
        return scopes;
    }

    @Deprecated // TBD - OPEN TICKET
    @JsonProperty("capabilities")
    public Set<Scope> getCapabilities() {
        return capabilities;
    }
}
