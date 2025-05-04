package com.extole.client.rest.webhook.reward.filter.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class SupplierRewardWebhookFilterCreateRequest {

    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";

    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId;

    public SupplierRewardWebhookFilterCreateRequest(
        @JsonProperty(REWARD_SUPPLIER_ID) BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId) {
        this.rewardSupplierId = rewardSupplierId;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static SupplierRewardWebhookFilterCreateRequestBuilder newRequestBuilder() {
        return new SupplierRewardWebhookFilterCreateRequestBuilder();
    }

    public static final class SupplierRewardWebhookFilterCreateRequestBuilder {

        private BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId;

        private SupplierRewardWebhookFilterCreateRequestBuilder() {

        }

        public SupplierRewardWebhookFilterCreateRequestBuilder
            withRewardSupplierId(BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId) {
            this.rewardSupplierId = rewardSupplierId;
            return this;
        }

        public SupplierRewardWebhookFilterCreateRequest build() {
            return new SupplierRewardWebhookFilterCreateRequest(rewardSupplierId);
        }
    }
}
