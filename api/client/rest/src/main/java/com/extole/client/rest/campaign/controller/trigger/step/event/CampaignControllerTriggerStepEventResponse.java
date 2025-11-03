package com.extole.client.rest.campaign.controller.trigger.step.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerStepEventResponse extends CampaignControllerTriggerResponse {

    private static final String EVENT_NAMES = "event_names";
    private static final String HAVING_ANY_DATA_NAME = "having_any_data_name";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> havingAnyDataName;

    public CampaignControllerTriggerStepEventResponse(
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
        @JsonProperty(EVENT_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames,
        @JsonProperty(HAVING_ANY_DATA_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<String>> havingAnyDataName,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.STEP_EVENT, triggerPhase, name, parentTriggerGroupName,
            description, enabled, negated, componentIds, componentReferences);
        this.eventNames = eventNames;
        this.havingAnyDataName = havingAnyDataName;
    }

    @JsonProperty(EVENT_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getEventNames() {
        return eventNames;
    }

    @JsonProperty(HAVING_ANY_DATA_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> getHavingAnyDataName() {
        return havingAnyDataName;
    }

}
