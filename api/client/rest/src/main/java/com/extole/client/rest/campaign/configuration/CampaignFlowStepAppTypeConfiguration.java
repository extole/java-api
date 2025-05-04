package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignFlowStepAppTypeConfiguration {

    private static final String JSON_NAME = "name";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;

    @JsonCreator
    public CampaignFlowStepAppTypeConfiguration(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
        this.name = name;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
