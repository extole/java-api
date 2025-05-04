package com.extole.client.rest.webhook.reward.filter.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.reward.filter.RewardWebhookFilterRuntimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class ExpressionRewardWebhookFilterCreateRequest {

    private static final String EXPRESSION = "expression";

    private final BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression;

    public ExpressionRewardWebhookFilterCreateRequest(
        @JsonProperty(EXPRESSION) BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression) {
        this.expression = expression;
    }

    @JsonProperty(EXPRESSION)
    public BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder newRequestBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression;

        private Builder() {

        }

        public Builder withExpression(BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression) {
            this.expression = expression;
            return this;
        }

        public ExpressionRewardWebhookFilterCreateRequest build() {
            return new ExpressionRewardWebhookFilterCreateRequest(expression);
        }
    }
}
