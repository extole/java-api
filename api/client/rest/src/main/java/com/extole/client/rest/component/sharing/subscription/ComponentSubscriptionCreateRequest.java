package com.extole.client.rest.component.sharing.subscription;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentSubscriptionCreateRequest {

    private static final String CLIENT_ID = "client_id";

    private final String clientId;

    @JsonCreator
    public ComponentSubscriptionCreateRequest(
        @JsonProperty(CLIENT_ID) String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String clientId;

        private Builder() {
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ComponentSubscriptionCreateRequest build() {
            return new ComponentSubscriptionCreateRequest(clientId);
        }
    }

}
