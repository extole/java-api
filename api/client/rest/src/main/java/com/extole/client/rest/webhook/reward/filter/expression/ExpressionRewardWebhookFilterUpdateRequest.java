package com.extole.client.rest.webhook.reward.filter.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.reward.filter.RewardWebhookFilterRuntimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class ExpressionRewardWebhookFilterUpdateRequest {

    private static final String EXPRESSION = "expression";

    private final Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>>> expression;

    public ExpressionRewardWebhookFilterUpdateRequest(
        @JsonProperty(EXPRESSION) Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>>> expression) {
        this.expression = expression;
    }

    @JsonProperty(EXPRESSION)
    public Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>>> getExpression() {
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

        private Omissible<BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>>> expression = Omissible.omitted();

        private Builder() {

        }

        public Builder withExpression(BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression) {
            this.expression = Omissible.of(expression);
            return this;
        }

        public ExpressionRewardWebhookFilterUpdateRequest build() {
            return new ExpressionRewardWebhookFilterUpdateRequest(expression);
        }
    }
}
