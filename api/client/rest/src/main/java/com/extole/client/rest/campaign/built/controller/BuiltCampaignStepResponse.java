package com.extole.client.rest.campaign.built.controller;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BuiltCampaignStepResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = BuiltCampaignFrontendControllerResponse.class,
        name = BuiltCampaignFrontendControllerResponse.STEP_TYPE_FRONTEND_CONTROLLER),
    @Type(value = BuiltCampaignControllerResponse.class,
        name = BuiltCampaignControllerResponse.STEP_TYPE_CONTROLLER),
    @Type(value = BuiltCampaignJourneyEntryResponse.class,
        name = BuiltCampaignJourneyEntryResponse.STEP_TYPE_JOURNEY_ENTRY)
})

@Schema(discriminatorProperty = BuiltCampaignStepResponse.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = BuiltCampaignFrontendControllerResponse.STEP_TYPE_FRONTEND_CONTROLLER,
        schema = BuiltCampaignFrontendControllerResponse.class),
    @DiscriminatorMapping(value = BuiltCampaignControllerResponse.STEP_TYPE_CONTROLLER,
        schema = BuiltCampaignControllerResponse.class),
    @DiscriminatorMapping(value = BuiltCampaignJourneyEntryResponse.STEP_TYPE_JOURNEY_ENTRY,
        schema = BuiltCampaignJourneyEntryResponse.class)
})
public abstract class BuiltCampaignStepResponse extends ComponentElementResponse {

    static final String JSON_TYPE = "type";
    static final String JSON_ID = "id";
    static final String JSON_CONTROLLER_ENABLED = "enabled";
    static final String JSON_TRIGGERS = "triggers";
    static final String JSON_CREATED_DATE = "created_date";
    static final String JSON_UPDATED_DATE = "updated_date";

    private final String id;
    private final boolean enabled;
    private final List<BuiltCampaignControllerTriggerResponse> triggers;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public BuiltCampaignStepResponse(
        String id,
        boolean enabled,
        List<BuiltCampaignControllerTriggerResponse> triggers,
        List<Id<ComponentResponse>> componentIds,
        List<ComponentReferenceResponse> componentReferences,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate) {
        super(componentReferences, componentIds);
        this.id = id;
        this.enabled = enabled;
        this.triggers = triggers != null ? ImmutableList.copyOf(triggers) : ImmutableList.of();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public abstract StepType getType();

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_CONTROLLER_ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_TRIGGERS)
    public List<BuiltCampaignControllerTriggerResponse> getTriggers() {
        return triggers;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

}
