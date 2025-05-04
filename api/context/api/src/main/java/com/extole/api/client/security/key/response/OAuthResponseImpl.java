package com.extole.api.client.security.key.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthResponseImpl implements OAuthResponse {

    private static final String JSON_ACCESS_TOKEN = "access_token";
    private static final String JSON_EXPIRES_IN = "expires_in";

    private final String accessToken;
    private final Long expiresIn;

    @JsonCreator
    public OAuthResponseImpl(
        @JsonProperty(JSON_ACCESS_TOKEN) String accessToken,
        @JsonProperty(JSON_EXPIRES_IN) Long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    @Override
    @JsonProperty(JSON_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    @JsonProperty(JSON_EXPIRES_IN)
    public Long getExpiresIn() {
        return expiresIn;
    }
}
