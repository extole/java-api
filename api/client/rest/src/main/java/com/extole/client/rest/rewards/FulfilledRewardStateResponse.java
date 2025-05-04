package com.extole.client.rest.rewards;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;

public class FulfilledRewardStateResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String COST_CODE = "cost_code";
    private static final String AMOUNT = "amount";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String CREATED_AT = "created_at";
    private static final String SUCCESS = "success";
    private static final String OPERATOR_USER_ID = "operator_user_id";
    private static final String MESSAGE = "message";

    private final String rewardId;
    private final String partnerRewardId;
    private final String costCode;
    private final BigDecimal amount;
    @Deprecated // TBD - OPEN TICKET
    private final BigDecimal faceValue;
    @Deprecated // TBD - OPEN TICKET
    private final FaceValueType faceValueType;
    private final ZonedDateTime createdAt;
    private final boolean success;
    private final String operatorUserId;
    private final String message;

    public FulfilledRewardStateResponse(@JsonProperty(REWARD_ID) String rewardId,
        @Nullable @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @Nullable @JsonProperty(COST_CODE) String costCode,
        @Nullable @JsonProperty(AMOUNT) BigDecimal amount,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(SUCCESS) boolean success,
        @JsonProperty(OPERATOR_USER_ID) String operatorUserId,
        @JsonProperty(MESSAGE) String message) {
        this.rewardId = rewardId;
        this.partnerRewardId = partnerRewardId;
        this.costCode = costCode;
        this.amount = amount;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
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
    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @Nullable
    @JsonProperty(COST_CODE)
    public String getCostCode() {
        return costCode;
    }

    @Nullable
    @JsonProperty(AMOUNT)
    public BigDecimal getAmount() {
        return amount;
    }

    @Deprecated // TBD - OPEN TICKET
    @Nullable
    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @Deprecated // TBD - OPEN TICKET
    @Nullable
    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
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
