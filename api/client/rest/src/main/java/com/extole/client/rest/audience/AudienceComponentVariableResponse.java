package com.extole.client.rest.audience;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AudienceComponentVariableResponse {

    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String COMPONENT_ID = "component_id";
    private static final String VARIABLE_NAME = "variable_name";
    private static final String AUDIENCE_ID = "audience_id";

    private final String campaignId;
    private final String componentId;
    private final String variableName;
    private final String audienceId;

    @JsonCreator
    public AudienceComponentVariableResponse(
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(COMPONENT_ID) String componentId,
        @JsonProperty(VARIABLE_NAME) String variableName,
        @JsonProperty(AUDIENCE_ID) String audienceId) {
        this.campaignId = campaignId;
        this.componentId = componentId;
        this.variableName = variableName;
        this.audienceId = audienceId;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(COMPONENT_ID)
    public String getComponentId() {
        return componentId;
    }

    @JsonProperty(VARIABLE_NAME)
    public String getVariableName() {
        return variableName;
    }

    @JsonProperty(AUDIENCE_ID)
    public String getAudienceId() {
        return audienceId;
    }

}
