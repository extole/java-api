package com.extole.client.rest.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthAccessTokenResponse {

    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_TOKEN_TYPE = "token_type";
    private static final String JSON_EXPIRES_IN = "expires_in";

    private final String accessToken;
    private final String tokenType;
    private final Long expiresIn;

    @JsonCreator
    public OAuthAccessTokenResponse(
        @JsonProperty(JSON_ACCESS_TOKEN) String accessToken,
        @JsonProperty(JSON_TOKEN_TYPE) String tokenType,
        @JsonProperty(JSON_EXPIRES_IN) Long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty(JSON_TOKEN_TYPE)
    public String getTokenType() {
        return tokenType;
    }

    @JsonProperty(JSON_EXPIRES_IN)
    public Long getExpiresIn() {
        return expiresIn;
    }
}
