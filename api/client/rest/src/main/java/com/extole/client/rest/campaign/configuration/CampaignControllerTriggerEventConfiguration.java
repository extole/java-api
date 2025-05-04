package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerEventConfiguration extends CampaignControllerTriggerConfiguration {
    private static final String EVENT_NAMES = "event_names";
    private static final String EVENT_TYPE = "event_type";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> eventType;

    public CampaignControllerTriggerEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(EVENT_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames,
        @JsonProperty(EVENT_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerEventType> eventType,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.EVENT, triggerPhase, name, description, enabled, negated,
            componentReferences);
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
