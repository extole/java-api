package com.extole.consumer.rest.me;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VerificationEmailRequest {
    private static final String JSON_CAMPAIGN_ID = "campaign_id";

    private final String campaignId;

    @JsonCreator
    public VerificationEmailRequest(@Nullable @JsonProperty(JSON_CAMPAIGN_ID) String campaignId) {
        this.campaignId = campaignId;
    }

    @Nullable
    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

}
