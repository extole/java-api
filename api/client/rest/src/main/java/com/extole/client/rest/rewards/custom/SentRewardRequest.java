package com.extole.client.rest.rewards.custom;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SentRewardRequest {

    private static final String PARTNER_REWARD_SENT_ID = "partner_reward_sent_id";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    private final String partnerRewardSentId;
    private final Boolean success;
    private final String message;

    public SentRewardRequest(@Nullable @JsonProperty(PARTNER_REWARD_SENT_ID) String partnerRewardSentId,
        @Nullable @JsonProperty(SUCCESS) Boolean success,
        @Nullable @JsonProperty(MESSAGE) String message) {
        this.partnerRewardSentId = partnerRewardSentId;
        this.success = success;
        this.message = message;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_SENT_ID)
    public String getPartnerRewardSentId() {
        return partnerRewardSentId;
    }

    @Nullable
    @JsonProperty(SUCCESS)
    public Boolean getSuccess() {
        return success;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
