package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerStepEventConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String EVENT_NAMES = "event_names";
    private static final String HAVING_ANY_DATA_NAME = "having_any_data_name";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> havingAnyDataName;

    public CampaignControllerTriggerStepEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
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
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.STEP_EVENT,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
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
