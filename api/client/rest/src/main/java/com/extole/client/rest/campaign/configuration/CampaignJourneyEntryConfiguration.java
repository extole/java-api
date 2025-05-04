package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignJourneyEntryConfiguration extends CampaignStepConfiguration {

    public static final String STEP_TYPE_JOURNEY_ENTRY = "JOURNEY_ENTRY";

    private static final String JSON_JOURNEY_NAME = "journey_name";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_KEY = "key";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> journeyName;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> priority;
    private final Optional<JourneyKeyConfiguration> key;

    @JsonCreator
    public CampaignJourneyEntryConfiguration(
        @JsonProperty(JSON_ID) Omissible<Id<CampaignStepConfiguration>> id,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_TRIGGERS) List<CampaignControllerTriggerConfiguration> triggers,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(JSON_JOURNEY_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> journeyName,
        @JsonProperty(JSON_PRIORITY) BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> priority,
        @JsonProperty(JSON_DATA) List<StepDataConfiguration> data,
        @JsonProperty(JSON_KEY) Optional<JourneyKeyConfiguration> key) {
        super(id, enabled, triggers, componentReferences, data);
        this.journeyName = journeyName;
        this.priority = priority;
        this.key = key;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    public StepType getType() {
        return StepType.JOURNEY_ENTRY;
    }

    @JsonProperty(JSON_JOURNEY_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JSON_PRIORITY)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> getPriority() {
        return priority;
    }

    @JsonProperty(JSON_KEY)
    public Optional<JourneyKeyConfiguration> getKey() {
        return key;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
