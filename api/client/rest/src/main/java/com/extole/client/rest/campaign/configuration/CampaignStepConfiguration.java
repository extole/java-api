package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignStepConfiguration.JSON_TYPE)
@JsonSubTypes({
    @Type(value = CampaignFrontendControllerConfiguration.class,
        name = CampaignFrontendControllerConfiguration.STEP_TYPE_FRONTEND_CONTROLLER),
    @Type(value = CampaignControllerConfiguration.class,
        name = CampaignControllerConfiguration.STEP_TYPE_CONTROLLER),
    @Type(value = CampaignJourneyEntryConfiguration.class,
        name = CampaignJourneyEntryConfiguration.STEP_TYPE_JOURNEY_ENTRY)
})

@Schema(discriminatorProperty = CampaignStepConfiguration.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CampaignFrontendControllerConfiguration.STEP_TYPE_FRONTEND_CONTROLLER,
        schema = CampaignFrontendControllerConfiguration.class),
    @DiscriminatorMapping(value = CampaignControllerConfiguration.STEP_TYPE_CONTROLLER,
        schema = CampaignControllerConfiguration.class),
    @DiscriminatorMapping(value = CampaignJourneyEntryConfiguration.STEP_TYPE_JOURNEY_ENTRY,
        schema = CampaignJourneyEntryConfiguration.class)
})
public abstract class CampaignStepConfiguration {

    static final String JSON_ID = "id";
    static final String JSON_TYPE = "type";
    static final String JSON_ENABLED = "enabled";
    static final String JSON_TRIGGERS = "triggers";
    static final String JSON_COMPONENT_REFERENCES = "component_references";
    static final String JSON_DATA = "data";

    private final Omissible<Id<CampaignStepConfiguration>> id;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled;
    private final List<CampaignControllerTriggerConfiguration> triggers;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;
    private final List<StepDataConfiguration> data;

    protected CampaignStepConfiguration(
        Omissible<Id<CampaignStepConfiguration>> id,
        BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        List<CampaignControllerTriggerConfiguration> triggers,
        List<CampaignComponentReferenceConfiguration> componentReferences,
        List<StepDataConfiguration> data) {
        this.id = id;
        this.enabled = enabled;
        this.triggers = triggers != null ? unmodifiableList(triggers) : Collections.emptyList();
        this.componentReferences =
            componentReferences != null ? unmodifiableList(componentReferences) : Collections.emptyList();
        this.data = data != null ? Collections.unmodifiableList(data) : Collections.emptyList();
    }

    public abstract StepType getType();

    @JsonProperty(JSON_ID)
    public Omissible<Id<CampaignStepConfiguration>> getId() {
        return id;
    }

    @JsonProperty(JSON_ENABLED)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_TRIGGERS)
    public List<CampaignControllerTriggerConfiguration> getTriggers() {
        return triggers;
    }

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @JsonProperty(JSON_DATA)
    public List<StepDataConfiguration> getData() {
        return data;
    }

}
