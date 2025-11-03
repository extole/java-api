package com.extole.client.rest.component.sharing.subscription;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentSubscriptionResponse {

    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String CLIENT_ID = "client_id";
    private static final String SUBSCRIBED_DATE = "subscribed_date";

    private final String subscriptionId;
    private final String clientId;
    private final ZonedDateTime subscribedDate;

    @JsonCreator
    public ComponentSubscriptionResponse(
        @JsonProperty(SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(SUBSCRIBED_DATE) ZonedDateTime subscribedDate) {
        this.subscriptionId = subscriptionId;
        this.clientId = clientId;
        this.subscribedDate = subscribedDate;
    }

    @JsonProperty(SUBSCRIPTION_ID)
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(SUBSCRIBED_DATE)
    public ZonedDateTime getSubscribedDate() {
        return subscribedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
