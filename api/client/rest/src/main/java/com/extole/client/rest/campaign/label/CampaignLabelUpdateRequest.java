package com.extole.client.rest.campaign.label;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampaignLabelUpdateRequest {
    private static final String JSON_LABEL_TYPE = "type";

    private final CampaignLabelType type;

    public CampaignLabelUpdateRequest(
        @JsonProperty(JSON_LABEL_TYPE) CampaignLabelType type) {
        this.type = type;
    }

    @JsonProperty(JSON_LABEL_TYPE)
    public CampaignLabelType getType() {
        return type;
    }

}
