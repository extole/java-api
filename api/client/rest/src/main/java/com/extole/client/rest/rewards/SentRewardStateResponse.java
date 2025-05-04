package com.extole.client.rest.rewards;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SentRewardStateResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String PARTNER_REWARD_SENT_ID = "partner_reward_sent_id";
    private static final String EMAIL = "email";
    private static final String CREATED_AT = "created_at";
    private static final String SUCCESS = "success";
    private static final String OPERATOR_USER_ID = "operator_user_id";
    private static final String MESSAGE = "message";

    private final String rewardId;
    private final String partnerRewardSentId;
    private final String email;
    private final ZonedDateTime createdAt;
    private final boolean success;
    private final String operatorUserId;
    private final String message;

    public SentRewardStateResponse(@JsonProperty(REWARD_ID) String rewardId,
        @Nullable @JsonProperty(PARTNER_REWARD_SENT_ID) String partnerRewardSentId,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(SUCCESS) boolean success,
        @JsonProperty(OPERATOR_USER_ID) String operatorUserId,
        @JsonProperty(MESSAGE) String message) {
        this.rewardId = rewardId;
        this.partnerRewardSentId = partnerRewardSentId;
        this.email = email;
        this.createdAt = createdAt;
        this.success = success;
        this.operatorUserId = operatorUserId;
        this.message = message;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_SENT_ID)
    public String getPartnerRewardSentId() {
        return partnerRewardSentId;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(SUCCESS)
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty(OPERATOR_USER_ID)
    public String getOperatorUserId() {
        return operatorUserId;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
