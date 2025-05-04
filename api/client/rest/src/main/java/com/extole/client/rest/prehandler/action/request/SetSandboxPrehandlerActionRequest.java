package com.extole.client.rest.prehandler.action.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.PrehandlerActionType;

@Schema(description = "Action that modifies the event sandbox.")
public class SetSandboxPrehandlerActionRequest extends PrehandlerActionRequest {
    static final String TYPE = "SET_SANDBOX";

    private static final String JSON_SANDBOX_ID = "sandbox_id";

    private final String sandboxId;

    @JsonCreator
    public SetSandboxPrehandlerActionRequest(
        @JsonProperty(JSON_SANDBOX_ID) String sandboxId) {
        super(PrehandlerActionType.SET_SANDBOX);
        this.sandboxId = sandboxId;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_SANDBOX_ID)
    @Schema(required = true, nullable = false, description = "Id of the sandbox to be set into the event.")
    public String getSandboxId() {
        return sandboxId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String sandboxId;

        public Builder withSandboxId(String sandboxId) {
            this.sandboxId = sandboxId;
            return this;
        }

        public SetSandboxPrehandlerActionRequest build() {
            return new SetSandboxPrehandlerActionRequest(sandboxId);
        }
    }
}
