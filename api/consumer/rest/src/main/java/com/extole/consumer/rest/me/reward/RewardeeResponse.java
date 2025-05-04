package com.extole.consumer.rest.me.reward;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RewardeeResponse {
    private static final String REWARDEE_NAME = "name";
    private static final String CAMPAIGN_ID = "campaign_id";

    private final String name;
    private final String campaignId;

    public RewardeeResponse(
        @JsonProperty(REWARDEE_NAME) String name,
        @JsonProperty(CAMPAIGN_ID) String campaignId) {
        this.name = name;
        this.campaignId = campaignId;
    }

    @JsonProperty(REWARDEE_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }
}
