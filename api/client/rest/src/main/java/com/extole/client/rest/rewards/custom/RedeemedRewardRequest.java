package com.extole.client.rest.rewards.custom;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RedeemedRewardRequest {

    private static final String PARTNER_REWARD_REDEEM_ID = "partner_reward_redeem_id";
    private static final String MESSAGE = "message";

    private final String partnerRewardRedeemId;
    private final String message;

    public RedeemedRewardRequest(@Nullable @JsonProperty(PARTNER_REWARD_REDEEM_ID) String partnerRewardRedeemId,
        @Nullable @JsonProperty(MESSAGE) String message) {
        this.partnerRewardRedeemId = partnerRewardRedeemId;
        this.message = message;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_REDEEM_ID)
    public String getPartnerRewardRedeemId() {
        return partnerRewardRedeemId;
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
