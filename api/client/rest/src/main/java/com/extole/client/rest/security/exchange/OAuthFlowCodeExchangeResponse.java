package com.extole.client.rest.security.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.ClientKey;
import com.extole.id.Id;

public class OAuthFlowCodeExchangeResponse {

    private static final String CLIENT_KEY_ID = "client_key_id";

    private final Id<ClientKey> clientKeyId;

    public OAuthFlowCodeExchangeResponse(@JsonProperty(CLIENT_KEY_ID) Id<ClientKey> clientKeyId) {
        this.clientKeyId = clientKeyId;
    }

    @JsonProperty(CLIENT_KEY_ID)
    public Id<ClientKey> getClientKeyId() {
        return clientKeyId;
    }
}
