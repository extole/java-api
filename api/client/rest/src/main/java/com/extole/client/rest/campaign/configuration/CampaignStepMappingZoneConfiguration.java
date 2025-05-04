package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignStepMappingZoneConfiguration {

    public static final String NAME = "name";
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;

    public CampaignStepMappingZoneConfiguration(
        @JsonProperty(NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
        this.name = name;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

}
