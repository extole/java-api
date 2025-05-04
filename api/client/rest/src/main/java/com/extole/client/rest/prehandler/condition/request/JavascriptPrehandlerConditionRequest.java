package com.extole.client.rest.prehandler.condition.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerConditionContext;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.id.JavascriptFunction;

@Schema(description = "Condition represented by Javascript. It evaluates to true if the javascript returns true.")
public class JavascriptPrehandlerConditionRequest extends PrehandlerConditionRequest {
    static final String TYPE = "JAVASCRIPT_V1";

    private static final String JSON_JAVASCRIPT = "javascript";

    private final JavascriptFunction<PrehandlerConditionContext, Boolean> javascript;

    @JsonCreator
    public JavascriptPrehandlerConditionRequest(
        @JsonProperty(JSON_JAVASCRIPT) JavascriptFunction<PrehandlerConditionContext, Boolean> javascript) {
        super(PrehandlerConditionType.JAVASCRIPT_V1);
        this.javascript = javascript;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_JAVASCRIPT)
    @Schema(required = true, nullable = false)
    public JavascriptFunction<PrehandlerConditionContext, Boolean> getJavascript() {
        return this.javascript;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private JavascriptFunction<PrehandlerConditionContext, Boolean> javascript;

        public Builder withJavascript(JavascriptFunction<PrehandlerConditionContext, Boolean> javascript) {
            this.javascript = javascript;
            return this;
        }

        public JavascriptPrehandlerConditionRequest build() {
            return new JavascriptPrehandlerConditionRequest(javascript);
        }
    }
}
