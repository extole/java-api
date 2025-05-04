package com.extole.client.rest.consumer;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsumerTokenResponse {
    private static final String JSON_PROPERTY_CLIENT_ID = "client_id";
    private static final String JSON_PROPERTY_ACCESS_TOKEN = "access_token";
    private static final String JSON_PROPERTY_SCOPES = "scopes";
    private final String accessToken;
    private final String clientId;
    private final Set<String> scopes;

    @JsonCreator
    public ConsumerTokenResponse(@JsonProperty(JSON_PROPERTY_ACCESS_TOKEN) String accessToken,
        @JsonProperty(JSON_PROPERTY_CLIENT_ID) String clientId,
        @JsonProperty(JSON_PROPERTY_SCOPES) Set<String> scopes) {
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.scopes = scopes;
    }

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_TOKEN)
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty(JSON_PROPERTY_SCOPES)
    public Set<String> getScopes() {
        return scopes;
    }

}
