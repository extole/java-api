package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerDataIntelligenceEventConfiguration
    extends CampaignControllerTriggerConfiguration {

    private static final String EVENT_NAME = "event_name";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName;

    public CampaignControllerTriggerDataIntelligenceEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(EVENT_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.DATA_INTELLIGENCE_EVENT, triggerPhase, name, description,
            enabled, negated, componentReferences);
        this.eventName = eventName;
    }

    @JsonProperty(EVENT_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getEventName() {
        return eventName;
    }

}
