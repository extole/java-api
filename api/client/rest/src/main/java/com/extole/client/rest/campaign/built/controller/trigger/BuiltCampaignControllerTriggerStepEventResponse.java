package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerStepEventResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String EVENT_NAMES = "event_names";
    private static final String HAVING_ANY_DATA_NAME = "having_any_data_name";

    private final Set<String> eventNames;
    private final Set<String> havingAnyDataName;

    public BuiltCampaignControllerTriggerStepEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Optional<String> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(EVENT_NAMES) Set<String> eventNames,
        @JsonProperty(HAVING_ANY_DATA_NAME) Set<String> havingAnyDataName,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.STEP_EVENT,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentIds,
            componentReferences);

        this.eventNames = ImmutableSet.copyOf(eventNames);
        this.havingAnyDataName = ImmutableSet.copyOf(havingAnyDataName);
    }

    @JsonProperty(EVENT_NAMES)
    public Set<String> getEventNames() {
        return eventNames;
    }

    @JsonProperty(HAVING_ANY_DATA_NAME)
    public Set<String> getHavingAnyDataName() {
        return havingAnyDataName;
    }

}
