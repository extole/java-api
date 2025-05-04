package com.extole.client.rest.zone;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignStepMappingZoneResponse {

    public static final String NAME = "name";
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;

    public CampaignStepMappingZoneResponse(
        @JsonProperty(NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
        this.name = name;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

}
