package com.extole.client.rest.webhook.reward.filter.supplier;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class SupplierRewardWebhookFilterResponse {

    private static final String WEBHOOK_FILTER_ID = "id";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public SupplierRewardWebhookFilterResponse(@JsonProperty(WEBHOOK_FILTER_ID) String id,
        @JsonProperty(REWARD_SUPPLIER_ID) BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.rewardSupplierId = rewardSupplierId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(WEBHOOK_FILTER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> getRewardSupplierId() {
        return rewardSupplierId;
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
