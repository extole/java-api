package com.extole.client.rest.prehandler.action.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerActionContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.evaluateable.Evaluatable;

@Schema(description = "Action represented by expression that can change the event.")
public class ExpressionPrehandlerActionRequest extends PrehandlerActionRequest {
    static final String TYPE = "EXPRESSION";

    private static final String JSON_EXPRESSION = "expression";

    private final Evaluatable<PrehandlerActionContext, Void> expression;

    @JsonCreator
    public ExpressionPrehandlerActionRequest(
        @JsonProperty(JSON_EXPRESSION) Evaluatable<PrehandlerActionContext, Void> expression) {
        super(PrehandlerActionType.EXPRESSION);
        this.expression = expression;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_EXPRESSION)
    @Schema(required = true, nullable = false)
    public Evaluatable<PrehandlerActionContext, Void> getExpression() {
        return this.expression;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Evaluatable<PrehandlerActionContext, Void> expression;

        public Builder withExpression(Evaluatable<PrehandlerActionContext, Void> expression) {
            this.expression = expression;
            return this;
        }

        public ExpressionPrehandlerActionRequest build() {
            return new ExpressionPrehandlerActionRequest(expression);
        }
    }
}
