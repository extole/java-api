package com.extole.client.rest.audience;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AudienceControllerTriggerResponse {

    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_CONTROLLER_ID = "controller_id";
    private static final String JSON_TRIGGER_ID = "trigger_id";
    private static final String JSON_AUDIENCE_ID = "audience_id";

    private final String campaignId;
    private final String controllerId;
    private final String triggerId;
    private final String audienceId;

    @JsonCreator
    public AudienceControllerTriggerResponse(
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CONTROLLER_ID) String controllerId,
        @JsonProperty(JSON_TRIGGER_ID) String triggerId,
        @JsonProperty(JSON_AUDIENCE_ID) String audienceId) {
        this.campaignId = campaignId;
        this.controllerId = controllerId;
        this.triggerId = triggerId;
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

    @JsonProperty(JSON_TRIGGER_ID)
    public String getTriggerId() {
        return triggerId;
    }

    @JsonProperty(JSON_AUDIENCE_ID)
    public String getAudienceId() {
        return audienceId;
    }

}
