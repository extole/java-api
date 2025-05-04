package com.extole.client.rest.campaign.built.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.id.Id;

public class BuiltCampaignJourneyEntryResponse extends BuiltCampaignStepResponse {

    public static final String STEP_TYPE_JOURNEY_ENTRY = "JOURNEY_ENTRY";

    private static final String JSON_JOURNEY_NAME = "journey_name";
    private static final String JSON_PRIORITY = "priority";
    private static final String JSON_KEY = "key";

    private final String journeyName;
    private final DeweyDecimal priority;
    private final Optional<BuiltJourneyKeyResponse> key;

    @JsonCreator
    public BuiltCampaignJourneyEntryResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_CONTROLLER_ENABLED) boolean enabled,
        @JsonProperty(JSON_TRIGGERS) List<BuiltCampaignControllerTriggerResponse> triggers,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_JOURNEY_NAME) String journeyName,
        @JsonProperty(JSON_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_KEY) Optional<BuiltJourneyKeyResponse> key) {
        super(id, enabled, triggers, componentIds, componentReferences, createdDate, updatedDate);
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
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JSON_PRIORITY)
    public DeweyDecimal getPriority() {
        return priority;
    }

    @JsonProperty(JSON_KEY)
    public Optional<BuiltJourneyKeyResponse> getKey() {
        return key;
    }

}
