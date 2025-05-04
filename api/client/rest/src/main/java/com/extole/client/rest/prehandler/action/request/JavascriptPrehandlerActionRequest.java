package com.extole.client.rest.prehandler.action.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerActionContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.id.JavascriptFunction;

@Schema(description = "Action represented by Javascript that can change the event.")
public class JavascriptPrehandlerActionRequest extends PrehandlerActionRequest {
    static final String TYPE = "JAVASCRIPT_V1";

    private static final String JSON_JAVASCRIPT = "javascript";

    private final JavascriptFunction<PrehandlerActionContext, Void> javascript;

    @JsonCreator
    public JavascriptPrehandlerActionRequest(
        @JsonProperty(JSON_JAVASCRIPT) JavascriptFunction<PrehandlerActionContext, Void> javascript) {
        super(PrehandlerActionType.JAVASCRIPT_V1);
        this.javascript = javascript;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_JAVASCRIPT)
    @Schema(required = true, nullable = false)
    public JavascriptFunction<PrehandlerActionContext, Void> getJavascript() {
        return this.javascript;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private JavascriptFunction<PrehandlerActionContext, Void> javascript;

        public Builder withJavascript(JavascriptFunction<PrehandlerActionContext, Void> javascript) {
            this.javascript = javascript;
            return this;
        }

        public JavascriptPrehandlerActionRequest build() {
            return new JavascriptPrehandlerActionRequest(javascript);
        }
    }
}
