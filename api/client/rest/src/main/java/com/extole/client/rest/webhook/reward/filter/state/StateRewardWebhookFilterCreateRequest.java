package com.extole.client.rest.webhook.reward.filter.state;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class StateRewardWebhookFilterCreateRequest {

    private static final String STATES = "states";

    private final Set<DetailedRewardState> states;

    public StateRewardWebhookFilterCreateRequest(@JsonProperty(STATES) Set<DetailedRewardState> states) {
        this.states = states;
    }

    @JsonProperty(STATES)
    public Set<DetailedRewardState> getStates() {
        return states;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static StateRewardWebhookFilterCreateRequestBuilder newRequestBuilder() {
        return new StateRewardWebhookFilterCreateRequestBuilder();
    }

    public static final class StateRewardWebhookFilterCreateRequestBuilder {

        private DetailedRewardState state;

        private StateRewardWebhookFilterCreateRequestBuilder() {

        }

        public StateRewardWebhookFilterCreateRequestBuilder withState(DetailedRewardState state) {
            this.state = state;
            return this;
        }

        public StateRewardWebhookFilterCreateRequest build() {
            return new StateRewardWebhookFilterCreateRequest(Collections.singleton(state));
        }
    }
}
