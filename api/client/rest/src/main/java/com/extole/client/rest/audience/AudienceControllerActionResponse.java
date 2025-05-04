package com.extole.client.rest.audience;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AudienceControllerActionResponse {

    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_CONTROLLER_ID = "controller_id";
    private static final String JSON_ACTION_ID = "action_id";
    private static final String JSON_AUDIENCE_ID = "audience_id";

    private final String campaignId;
    private final String controllerId;
    private final String actionId;
    private final String audienceId;

    @JsonCreator
    public AudienceControllerActionResponse(
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CONTROLLER_ID) String controllerId,
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_AUDIENCE_ID) String audienceId) {
        this.campaignId = campaignId;
        this.controllerId = controllerId;
        this.actionId = actionId;
        this.audienceId = audienceId;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_CONTROLLER_ID)
    public String getControllerId() {
        return controllerId;
    }

    @JsonProperty(JSON_ACTION_ID)
    public String getActionId() {
        return actionId;
    }

    @JsonProperty(JSON_AUDIENCE_ID)
    public String getAudienceId() {
        return audienceId;
    }

}
