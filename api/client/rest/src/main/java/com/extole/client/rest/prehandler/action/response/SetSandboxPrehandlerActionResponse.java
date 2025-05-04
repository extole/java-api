package com.extole.client.rest.prehandler.action.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.PrehandlerActionType;

@Schema(description = "Action that modifies the event sandbox.")
public class SetSandboxPrehandlerActionResponse extends PrehandlerActionResponse {
    static final String TYPE = "SET_SANDBOX";

    private static final String JSON_SANDBOX_ID = "sandbox_id";

    private final String sandboxId;

    @JsonCreator
    public SetSandboxPrehandlerActionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_SANDBOX_ID) String sandboxId) {
        super(id, PrehandlerActionType.SET_SANDBOX);
        this.sandboxId = sandboxId;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_SANDBOX_ID)
    @Schema(nullable = false)
    public String getSandboxId() {
        return sandboxId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String sandboxId;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withSandboxId(String sandboxId) {
            this.sandboxId = sandboxId;
            return this;
        }

        public SetSandboxPrehandlerActionResponse build() {
            return new SetSandboxPrehandlerActionResponse(id, sandboxId);
        }
    }
}
