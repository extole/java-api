package com.extole.client.rest.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientVersionResponse {
    private static final String CLIENT_ID = "client_id";
    private static final String VERSION = "version";
    private static final String CHANGES_PENDING = "changes_pending";

    private final String clientId;
    private final Integer version;
    private final boolean changesPending;

    @JsonCreator
    public ClientVersionResponse(@JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(VERSION) Integer version,
        @JsonProperty(CHANGES_PENDING) boolean changesPending) {
        this.clientId = clientId;
        this.version = version;
        this.changesPending = changesPending;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(VERSION)
    public Integer getVersion() {
        return version;
    }

    @JsonProperty(CHANGES_PENDING)
    public boolean getChangesPending() {
        return changesPending;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
