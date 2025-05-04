package com.extole.client.rest.prehandler.condition.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerConditionContext;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.id.JavascriptFunction;

@Schema(description = "Condition represented by Javascript. It evaluates to true if the javascript returns true.")
public class JavascriptPrehandlerConditionResponse extends PrehandlerConditionResponse {
    static final String TYPE = "JAVASCRIPT_V1";

    private static final String JSON_JAVASCRIPT = "javascript";

    private final JavascriptFunction<PrehandlerConditionContext, Boolean> javascript;

    @JsonCreator
    public JavascriptPrehandlerConditionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_JAVASCRIPT) JavascriptFunction<PrehandlerConditionContext, Boolean> javascript) {
        super(id, PrehandlerConditionType.JAVASCRIPT_V1);
        this.javascript = javascript;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_JAVASCRIPT)
    @Schema(nullable = false)
    public JavascriptFunction<PrehandlerConditionContext, Boolean> getJavascript() {
        return this.javascript;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private JavascriptFunction<PrehandlerConditionContext, Boolean> javascript;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withJavascript(JavascriptFunction<PrehandlerConditionContext, Boolean> javascript) {
            this.javascript = javascript;
            return this;
        }

        public JavascriptPrehandlerConditionResponse build() {
            return new JavascriptPrehandlerConditionResponse(id, javascript);
        }
    }
}
