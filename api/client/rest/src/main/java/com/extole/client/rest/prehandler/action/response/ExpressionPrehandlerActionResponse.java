package com.extole.client.rest.prehandler.action.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerActionContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.evaluateable.Evaluatable;

@Schema(description = "Action represented by expression that can change the event.")
public class ExpressionPrehandlerActionResponse extends PrehandlerActionResponse {
    static final String TYPE = "EXPRESSION";

    private static final String JSON_EXPRESSION = "expression";

    private final Evaluatable<PrehandlerActionContext, Void> expression;

    @JsonCreator
    public ExpressionPrehandlerActionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_EXPRESSION) Evaluatable<PrehandlerActionContext, Void> expression) {
        super(id, PrehandlerActionType.EXPRESSION);
        this.expression = expression;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_EXPRESSION)
    @Schema(nullable = false)
    public Evaluatable<PrehandlerActionContext, Void> getExpression() {
        return this.expression;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Evaluatable<PrehandlerActionContext, Void> expression;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withExpression(Evaluatable<PrehandlerActionContext, Void> expression) {
            this.expression = expression;
            return this;
        }

        public ExpressionPrehandlerActionResponse build() {
            return new ExpressionPrehandlerActionResponse(id, expression);
        }
    }
}
