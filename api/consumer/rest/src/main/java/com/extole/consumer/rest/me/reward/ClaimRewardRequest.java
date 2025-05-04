package com.extole.consumer.rest.me.reward;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClaimRewardRequest {
    public static final String REWARDEE_ADVOCATE = "advocate_reward";
    public static final String REWARDEE_FRIEND = "friend_reward";

    private static final String CAMPAIGN_ID = "campaign_id";
    @Deprecated // TODO cleanup ENG-10142
    private static final String REWARDER_NAME = "rewarder_name";

    private final String campaignId;
    private final String rewardeeName;

    @JsonCreator
    public ClaimRewardRequest(
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(REWARDER_NAME) String rewardeeName) {
        this.campaignId = campaignId;
        this.rewardeeName = rewardeeName;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(REWARDER_NAME)
    public String getRewardeeName() {
        return rewardeeName;
    }

}
