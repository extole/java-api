package com.extole.client.rest.rewards;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RewardStateResponse {

    private static final String STATE_TYPE = "state_type";
    private static final String SUCCESS = "success";
    private static final String OPERATOR_USER_ID = "operator_user_id";
    private static final String MESSAGE = "message";
    private static final String CREATED_AT = "created_at";

    private final RewardStateType stateType;
    private final String message;
    private final boolean success;
    private final String operatorUserId;
    private final ZonedDateTime createdAt;

    public RewardStateResponse(@JsonProperty(STATE_TYPE) RewardStateType stateType,
        @JsonProperty(MESSAGE) String message,
        @JsonProperty(SUCCESS) boolean success,
        @JsonProperty(OPERATOR_USER_ID) String operatorUserId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt) {
        this.stateType = stateType;
        this.message = message;
        this.success = success;
        this.operatorUserId = operatorUserId;
        this.createdAt = createdAt;
    }

    @JsonProperty(STATE_TYPE)
    public RewardStateType getStateType() {
        return stateType;
    }

    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(SUCCESS)
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty(OPERATOR_USER_ID)
    public String getOperatorUserId() {
        return operatorUserId;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
