package com.extole.client.rest.campaign.controller.response;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignJourneyEntryResponse extends CampaignStepResponse {

    public static final String STEP_TYPE_JOURNEY_ENTRY = "JOURNEY_ENTRY";

    private static final String JSON_JOURNEY_NAME = "journey_name";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_KEY = "key";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> journeyName;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> priority;
    private final Optional<JourneyKeyResponse> key;

    @JsonCreator
    public CampaignJourneyEntryResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_TRIGGERS) List<CampaignControllerTriggerResponse> triggers,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_JOURNEY_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> journeyName,
        @JsonProperty(JSON_PRIORITY) BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> priority,
        @JsonProperty(JSON_DATA) List<StepDataResponse> data,
        @JsonProperty(JSON_KEY) Optional<JourneyKeyResponse> key) {
        super(id, enabled, triggers, componentIds, componentReferences, createdDate, updatedDate, data);
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
    public Optional<JourneyKeyResponse> getKey() {
        return key;
    }

}
