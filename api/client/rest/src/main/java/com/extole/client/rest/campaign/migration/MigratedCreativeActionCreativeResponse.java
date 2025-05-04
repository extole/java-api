package com.extole.client.rest.campaign.migration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MigratedCreativeActionCreativeResponse extends MigratedCreativeResponse {
    public static final String TYPE = "CREATIVE_ACTION";
    private static final String CONTROLLER_ID = "controller_id";
    private static final String ACTION_ID = "action_id";
    private static final String TRIGGER_ID = "trigger_id";

    private final String controllerId;
    private final String actionId;
    private final String triggerId;

    @JsonCreator
    public MigratedCreativeActionCreativeResponse(
        @JsonProperty(CONTROLLER_ID) String controllerId,
        @JsonProperty(ACTION_ID) String actionId,
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(CREATIVE_ID) String creativeId,
        @JsonProperty(COMPONENT_NAME) String componentName,
        @JsonProperty(DEDUPED_LEGACY_VARIABLES) List<String> dedupedLegacyVariables) {
        super(creativeId, componentName, dedupedLegacyVariables);
        this.controllerId = controllerId;
        this.actionId = actionId;
        this.triggerId = triggerId;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public MigratedCreativeResponseType getType() {
        return MigratedCreativeResponseType.CREATIVE_ACTION;
    }

    @JsonProperty(CONTROLLER_ID)
    public String getControllerId() {
        return controllerId;
    }

    @JsonProperty(ACTION_ID)
    public String getActionId() {
        return actionId;
    }

    @JsonProperty(TRIGGER_ID)
    public String getTriggerId() {
        return triggerId;
    }
}
