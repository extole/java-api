package com.extole.client.rest.campaign.incentive.reward.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RewardRuleExpression {
    private static final String VALUE = "value";
    private static final String TYPE = "type";

    private final String value;
    private final ExpressionType type;

    public RewardRuleExpression(
        @JsonProperty(VALUE) String value,
        @JsonProperty(TYPE) ExpressionType type) {
        this.value = value;
        this.type = type;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @JsonProperty(TYPE)
    public ExpressionType getType() {
        return type;
    }

    public static RequestRewardRuleExpressionBuilder builder() {
        return new RequestRewardRuleExpressionBuilder();
    }

    public static final class RequestRewardRuleExpressionBuilder {
        private String value;
        private ExpressionType type;

        private RequestRewardRuleExpressionBuilder() {
        }

        public RequestRewardRuleExpressionBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public RequestRewardRuleExpressionBuilder withType(ExpressionType type) {
            this.type = type;
            return this;
        }

        public RewardRuleExpression build() {
            return new RewardRuleExpression(value, type);
        }
    }
}
