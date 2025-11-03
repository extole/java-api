package com.extole.client.rest.campaign.controller.trigger.event;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerEventResponse extends CampaignControllerTriggerResponse {
    private static final String EVENT_NAMES = "event_names";
    private static final String EVENT_TYPE = "event_type";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> eventType;

    public CampaignControllerTriggerEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(EVENT_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames,
        @JsonProperty(EVENT_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerEventType> eventType,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.EVENT, triggerPhase, name, parentTriggerGroupName, description,
            enabled, negated, componentIds, componentReferences);
        this.eventNames = eventNames;
        this.eventType = eventType;
    }

    @JsonProperty(EVENT_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> getEventNames() {
        return eventNames;
    }

    @JsonProperty(EVENT_TYPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> getEventType() {
        return eventType;
    }
}
