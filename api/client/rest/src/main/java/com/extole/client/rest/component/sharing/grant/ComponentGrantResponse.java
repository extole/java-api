package com.extole.client.rest.component.sharing.grant;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ComponentGrantResponse {

    private static final String GRANT_ID = "grant_id";
    private static final String CLIENT_ID = "client_id";
    private static final String SUBSCRIPTION_MODE = "subscription_mode";
    private static final String GRANTED_DATE = "granted_date";

    private final String grantId;
    private final String clientId;
    private final SubscriptionMode subscriptionMode;
    private final ZonedDateTime grantedDate;

    @JsonCreator
    public ComponentGrantResponse(
        @JsonProperty(GRANT_ID) String grantId,
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(SUBSCRIPTION_MODE) SubscriptionMode subscriptionMode,
        @JsonProperty(GRANTED_DATE) ZonedDateTime grantedDate) {
        this.grantId = grantId;
        this.clientId = clientId;
        this.subscriptionMode = subscriptionMode;
        this.grantedDate = grantedDate;
    }

    @JsonProperty(GRANT_ID)
    public String getGrantId() {
        return grantId;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(SUBSCRIPTION_MODE)
    public SubscriptionMode getSubscriptionMode() {
        return subscriptionMode;
    }

    @JsonProperty(GRANTED_DATE)
    public ZonedDateTime getGrantedDate() {
        return grantedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
