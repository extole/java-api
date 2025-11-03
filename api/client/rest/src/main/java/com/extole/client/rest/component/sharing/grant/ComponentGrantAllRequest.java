package com.extole.client.rest.component.sharing.grant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ComponentGrantAllRequest {

    private static final String SUBSCRIPTION_MODE = "subscription_mode";

    private final Omissible<SubscriptionMode> subscriptionMode;

    @JsonCreator
    public ComponentGrantAllRequest(@JsonProperty(SUBSCRIPTION_MODE) Omissible<SubscriptionMode> subscriptionMode) {
        this.subscriptionMode = subscriptionMode;
    }

    @JsonProperty(SUBSCRIPTION_MODE)
    public Omissible<SubscriptionMode> getSubscriptionMode() {
        return subscriptionMode;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Omissible<SubscriptionMode> subscriptionMode = Omissible.omitted();

        private Builder() {
        }

        public Builder withSubscriptionMode(SubscriptionMode subscriptionMode) {
            this.subscriptionMode = Omissible.of(subscriptionMode);
            return this;
        }

        public ComponentGrantAllRequest build() {
            return new ComponentGrantAllRequest(subscriptionMode);
        }
    }

}
