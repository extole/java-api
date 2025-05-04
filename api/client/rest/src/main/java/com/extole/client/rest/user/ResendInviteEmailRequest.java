package com.extole.client.rest.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ResendInviteEmailRequest {
    private static final String ACCESS_TOKEN = "access_token";
    private final String accessToken;

    public ResendInviteEmailRequest(@JsonProperty(ACCESS_TOKEN) String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty(ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
