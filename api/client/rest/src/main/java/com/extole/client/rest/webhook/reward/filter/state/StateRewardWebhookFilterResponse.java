package com.extole.client.rest.webhook.reward.filter.state;

import java.time.ZonedDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class StateRewardWebhookFilterResponse {

    private static final String WEBHOOK_FILTER_ID = "id";
    private static final String STATES = "states";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final Set<DetailedRewardState> states;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public StateRewardWebhookFilterResponse(@JsonProperty(WEBHOOK_FILTER_ID) String id,
        @JsonProperty(STATES) Set<DetailedRewardState> states,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.states = states;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(WEBHOOK_FILTER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(STATES)
    public Set<DetailedRewardState> getStates() {
        return states;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(UPDATED_AT)
    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
