package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.event.CampaignControllerTriggerEventType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerEventResponse extends BuiltCampaignControllerTriggerResponse {
    private static final String EVENT_NAMES = "event_names";
    private static final String EVENT_TYPE = "event_type";

    private final List<String> eventNames;
    private final CampaignControllerTriggerEventType eventType;

    public BuiltCampaignControllerTriggerEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(EVENT_NAMES) List<String> eventNames,
        @JsonProperty(EVENT_TYPE) CampaignControllerTriggerEventType eventType,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.EVENT, triggerPhase, name, description, enabled, negated,
            componentIds, componentReferences);
        this.eventNames = eventNames;
        this.eventType = eventType;
    }

    @JsonProperty(EVENT_NAMES)
    public List<String> getEventNames() {
        return eventNames;
    }

    @JsonProperty(EVENT_TYPE)
    public CampaignControllerTriggerEventType getEventType() {
        return eventType;
    }
}
