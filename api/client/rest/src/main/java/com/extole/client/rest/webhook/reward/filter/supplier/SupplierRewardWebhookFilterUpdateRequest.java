package com.extole.client.rest.webhook.reward.filter.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class SupplierRewardWebhookFilterUpdateRequest {

    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";

    private final BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId;

    public SupplierRewardWebhookFilterUpdateRequest(
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

    public static SupplierRewardWebhookFilterUpdateRequestBuilder newRequestBuilder() {
        return new SupplierRewardWebhookFilterUpdateRequestBuilder();
    }

    public static final class SupplierRewardWebhookFilterUpdateRequestBuilder {

        private BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId;

        private SupplierRewardWebhookFilterUpdateRequestBuilder() {

        }

        public SupplierRewardWebhookFilterUpdateRequestBuilder
            withRewardSupplierId(BuildtimeEvaluatable<WebhookBuildtimeContext, Id<?>> rewardSupplierId) {
            this.rewardSupplierId = rewardSupplierId;
            return this;
        }

        public SupplierRewardWebhookFilterUpdateRequest build() {
            return new SupplierRewardWebhookFilterUpdateRequest(rewardSupplierId);
        }
    }
}
