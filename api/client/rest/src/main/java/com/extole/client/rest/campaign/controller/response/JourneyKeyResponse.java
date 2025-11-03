package com.extole.client.rest.campaign.controller.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.journey.JourneyKeyContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class JourneyKeyResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value;

    public JourneyKeyResponse(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_VALUE) BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>> value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<JourneyKeyContext, Optional<Object>>>
        getValue() {
        return value;
    }

}
