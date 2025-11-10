package com.extole.client.rest.component.sharing.subscription.built;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltComponentSubscriptionResponse extends ComponentElementResponse {

    private static final String SUBSCRIPTION_ID = "subscription_id";
    private static final String CLIENT_ID = "client_id";
    private static final String SUBSCRIBED_DATE = "subscribed_date";

    private final String subscriptionId;
    private final String clientId;
    private final ZonedDateTime subscribedDate;

    @JsonCreator
    public BuiltComponentSubscriptionResponse(
        @JsonProperty(SUBSCRIPTION_ID) String subscriptionId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(SUBSCRIBED_DATE) ZonedDateTime subscribedDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
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
