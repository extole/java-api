package com.extole.client.rest.me;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeClientResponse {

    private static final String CLIENT_ID = "client_id";
    private static final String NAME = "name";

    private final String clientId;
    private final String name;

    public MeClientResponse(@JsonProperty(CLIENT_ID) String clientId, @JsonProperty(NAME) String name) {
        this.clientId = clientId;
        this.name = name;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

}
