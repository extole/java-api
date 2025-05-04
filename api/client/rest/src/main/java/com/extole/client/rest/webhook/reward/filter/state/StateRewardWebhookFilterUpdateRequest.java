package com.extole.client.rest.webhook.reward.filter.state;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class StateRewardWebhookFilterUpdateRequest {

    private static final String STATES = "states";

    private final Set<DetailedRewardState> states;

    public StateRewardWebhookFilterUpdateRequest(@Nullable @JsonProperty(STATES) Set<DetailedRewardState> states) {
        this.states = states;
    }

    @Nullable
    @JsonProperty(STATES)
    public Set<DetailedRewardState> getStates() {
        return states;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static StateRewardWebhookFilterUpdateRequestBuilder newRequestBuilder() {
        return new StateRewardWebhookFilterUpdateRequestBuilder();
    }

    public static final class StateRewardWebhookFilterUpdateRequestBuilder {

        private DetailedRewardState state;

        private StateRewardWebhookFilterUpdateRequestBuilder() {

        }

        public StateRewardWebhookFilterUpdateRequestBuilder withState(DetailedRewardState state) {
            this.state = state;
            return this;
        }

        public StateRewardWebhookFilterUpdateRequest build() {
            return new StateRewardWebhookFilterUpdateRequest(Collections.singleton(state));
        }
    }
}
