package com.extole.client.rest.rewards;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CanceledRewardStateResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String CREATED_AT = "created_at";
    private static final String OPERATOR_USER_ID = "operator_user_id";
    private static final String MESSAGE = "message";

    private final String rewardId;
    private final ZonedDateTime createdAt;
    private final String operatorUserId;
    private final String message;

    public CanceledRewardStateResponse(@JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(OPERATOR_USER_ID) String operatorUserId,
        @JsonProperty(MESSAGE) String message) {
        this.rewardId = rewardId;
        this.createdAt = createdAt;
        this.operatorUserId = operatorUserId;
        this.message = message;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
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
