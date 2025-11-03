package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerScoreResponse extends BuiltCampaignControllerTriggerResponse {
    private static final String SCORE_RESULT = "score_result";
    private static final String CAUSE_EVENT_NAME = "cause_event_name";
    private static final String CHANNEL = "channel";

    private final String scoreResult;
    private final String causeEventName;
    private final String channel;

    public BuiltCampaignControllerTriggerScoreResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(SCORE_RESULT) String scoreResult,
        @JsonProperty(CAUSE_EVENT_NAME) String causeEventName,
        @JsonProperty(CHANNEL) String channel,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.SCORE,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);
        this.scoreResult = scoreResult;
        this.causeEventName = causeEventName;
        this.channel = channel;
    }

    @JsonProperty(CAUSE_EVENT_NAME)
    public String getCauseEventName() {
        return causeEventName;
    }

    @JsonProperty(SCORE_RESULT)
    public String getScoreResult() {
        return scoreResult;
    }

    @JsonProperty(CHANNEL)
    public String getChannel() {
        return channel;
    }
}
