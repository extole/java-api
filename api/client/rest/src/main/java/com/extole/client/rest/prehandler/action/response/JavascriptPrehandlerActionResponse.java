package com.extole.client.rest.prehandler.action.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerActionContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.id.JavascriptFunction;

@Schema(description = "Action represented by Javascript that can change the event.")
public class JavascriptPrehandlerActionResponse extends PrehandlerActionResponse {
    static final String TYPE = "JAVASCRIPT_V1";

    private static final String JSON_JAVASCRIPT = "javascript";

    private final JavascriptFunction<PrehandlerActionContext, Void> javascript;

    @JsonCreator
    public JavascriptPrehandlerActionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_JAVASCRIPT) JavascriptFunction<PrehandlerActionContext, Void> javascript) {
        super(id, PrehandlerActionType.JAVASCRIPT_V1);
        this.javascript = javascript;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_JAVASCRIPT)
    @Schema(nullable = false)
    public JavascriptFunction<PrehandlerActionContext, Void> getJavascript() {
        return this.javascript;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private JavascriptFunction<PrehandlerActionContext, Void> javascript;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withJavascript(JavascriptFunction<PrehandlerActionContext, Void> javascript) {
            this.javascript = javascript;
            return this;
        }

        public JavascriptPrehandlerActionResponse build() {
            return new JavascriptPrehandlerActionResponse(id, javascript);
        }
    }
}
