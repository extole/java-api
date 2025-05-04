package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignFlowStepAppConfiguration {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TYPE = "type";
    private static final String JSON_COMPONENT_REFERENCES = "component_references";

    private final Omissible<Id<CampaignFlowStepAppConfiguration>> id;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description;
    private final CampaignFlowStepAppTypeConfiguration type;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;

    @JsonCreator
    public CampaignFlowStepAppConfiguration(
        @JsonProperty(JSON_ID) Omissible<Id<CampaignFlowStepAppConfiguration>> id,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_TYPE) CampaignFlowStepAppTypeConfiguration type,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.componentReferences = componentReferences != null ? unmodifiableList(componentReferences) : List.of();
    }

    @JsonProperty(JSON_ID)
    public Omissible<Id<CampaignFlowStepAppConfiguration>> getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_TYPE)
    public CampaignFlowStepAppTypeConfiguration getType() {
        return type;
    }

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
