package com.extole.client.rest.webhook.reward.filter.expression;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.webhook.built.WebhookBuildtimeContext;
import com.extole.api.webhook.reward.filter.RewardWebhookFilterRuntimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class ExpressionRewardWebhookFilterResponse {

    private static final String WEBHOOK_FILTER_ID = "id";
    private static final String EXPRESSION = "expression";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    private final String id;
    private final BuildtimeEvaluatable<WebhookBuildtimeContext,
        RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public ExpressionRewardWebhookFilterResponse(@JsonProperty(WEBHOOK_FILTER_ID) String id,
        @JsonProperty(EXPRESSION) BuildtimeEvaluatable<WebhookBuildtimeContext,
            RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>> expression,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(UPDATED_AT) ZonedDateTime updatedAt) {
        this.id = id;
        this.expression = expression;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @JsonProperty(WEBHOOK_FILTER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(EXPRESSION)
    public BuildtimeEvaluatable<WebhookBuildtimeContext, RuntimeEvaluatable<RewardWebhookFilterRuntimeContext, Boolean>>
        getExpression() {
        return expression;
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
