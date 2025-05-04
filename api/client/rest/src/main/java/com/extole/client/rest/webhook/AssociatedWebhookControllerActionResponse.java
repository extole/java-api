package com.extole.client.rest.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AssociatedWebhookControllerActionResponse {

    private static final String JSON_ACTION_ID = "action_id";
    private static final String JSON_CONTROLLER_ID = "controller_id";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";

    private final String campaignId;
    private final String controllerId;
    private final String actionId;

    @JsonCreator
    public AssociatedWebhookControllerActionResponse(
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CONTROLLER_ID) String controllerId,
        @JsonProperty(JSON_ACTION_ID) String actionId) {
        this.campaignId = campaignId;
        this.controllerId = controllerId;
        this.actionId = actionId;
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

}
