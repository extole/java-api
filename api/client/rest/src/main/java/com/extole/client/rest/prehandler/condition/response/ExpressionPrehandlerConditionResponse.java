package com.extole.client.rest.prehandler.condition.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerConditionContext;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.evaluateable.Evaluatable;

@Schema(description = "Condition represented by expression. It evaluates to true if the expression returns true.")
public class ExpressionPrehandlerConditionResponse extends PrehandlerConditionResponse {
    static final String TYPE = "EXPRESSION";

    private static final String JSON_EXPRESSION = "expression";

    private final Evaluatable<PrehandlerConditionContext, Boolean> expression;

    @JsonCreator
    public ExpressionPrehandlerConditionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_EXPRESSION) Evaluatable<PrehandlerConditionContext, Boolean> expression) {
        super(id, PrehandlerConditionType.EXPRESSION);
        this.expression = expression;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_EXPRESSION)
    @Schema(nullable = false)
    public Evaluatable<PrehandlerConditionContext, Boolean> getExpression() {
        return this.expression;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Evaluatable<PrehandlerConditionContext, Boolean> expression;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withExpression(Evaluatable<PrehandlerConditionContext, Boolean> expression) {
            this.expression = expression;
            return this;
        }

        public ExpressionPrehandlerConditionResponse build() {
            return new ExpressionPrehandlerConditionResponse(id, expression);
        }
    }
}
