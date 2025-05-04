package com.extole.client.rest.webhook.reward.filter;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RewardWebhookFilterResponse {

    private static final String WEBHOOK_FILTER_ID = "id";
    private static final String TYPE = "type";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final RewardWebhookFilterType type;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public RewardWebhookFilterResponse(@JsonProperty(WEBHOOK_FILTER_ID) String id,
        @JsonProperty(TYPE) RewardWebhookFilterType type,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(WEBHOOK_FILTER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(TYPE)
    public RewardWebhookFilterType getType() {
        return type;
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
