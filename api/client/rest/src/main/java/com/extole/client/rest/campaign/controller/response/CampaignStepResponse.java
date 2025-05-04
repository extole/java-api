package com.extole.client.rest.campaign.controller.response;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignStepResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = CampaignFrontendControllerResponse.class,
        name = CampaignFrontendControllerResponse.STEP_TYPE_FRONTEND_CONTROLLER),
    @Type(value = CampaignControllerResponse.class,
        name = CampaignControllerResponse.STEP_TYPE_CONTROLLER),
    @Type(value = CampaignJourneyEntryResponse.class,
        name = CampaignJourneyEntryResponse.STEP_TYPE_JOURNEY_ENTRY)
})

@Schema(discriminatorProperty = CampaignStepResponse.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CampaignFrontendControllerResponse.STEP_TYPE_FRONTEND_CONTROLLER,
        schema = CampaignFrontendControllerResponse.class),
    @DiscriminatorMapping(value = CampaignControllerResponse.STEP_TYPE_CONTROLLER,
        schema = CampaignControllerResponse.class),
    @DiscriminatorMapping(value = CampaignJourneyEntryResponse.STEP_TYPE_JOURNEY_ENTRY,
        schema = CampaignJourneyEntryResponse.class)
})
public abstract class CampaignStepResponse extends ComponentElementResponse {

    static final String JSON_TYPE = "type";
    static final String JSON_ID = "id";
    static final String JSON_ENABLED = "enabled";
    static final String JSON_TRIGGERS = "triggers";
    static final String JSON_CREATED_DATE = "created_date";
    static final String JSON_UPDATED_DATE = "updated_date";
    static final String JSON_DATA = "data";

    private final String id;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled;
    private final List<CampaignControllerTriggerResponse> triggers;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final List<StepDataResponse> data;

    protected CampaignStepResponse(
        String id,
        BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        List<CampaignControllerTriggerResponse> triggers,
        List<Id<ComponentResponse>> componentIds,
        List<ComponentReferenceResponse> componentReferences,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate,
        List<StepDataResponse> data) {
        super(componentReferences, componentIds);
        this.id = id;
        this.enabled = enabled;
        this.triggers = triggers != null ? ImmutableList.copyOf(triggers) : ImmutableList.of();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
    }

    public abstract StepType getType();

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_ENABLED)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_TRIGGERS)
    public List<CampaignControllerTriggerResponse> getTriggers() {
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

    @JsonProperty(JSON_DATA)
    public List<StepDataResponse> getData() {
        return data;
    }

}
