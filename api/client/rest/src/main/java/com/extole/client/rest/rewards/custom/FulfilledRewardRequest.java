package com.extole.client.rest.rewards.custom;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;

public class FulfilledRewardRequest {

    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    @Deprecated // TODO remove/move ENG-13798
    private static final String COST_CODE = "cost_code";
    @Deprecated // TODO remove/move ENG-13798
    private static final String FACE_VALUE = "face_value";
    @Deprecated // TODO remove/move ENG-13798
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    private final String partnerRewardId;
    @Deprecated // TODO remove/move ENG-13798
    private final String costCode;
    @Deprecated // TODO remove/move ENG-13798
    private final BigDecimal faceValue;
    @Deprecated // TODO remove/move ENG-13798
    private final FaceValueType faceValueType;
    private final Boolean success;
    private final String message;

    public FulfilledRewardRequest(@Nullable @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @Deprecated // TODO remove/move ENG-13798
        @Nullable @JsonProperty(COST_CODE) String costCode,
        @Deprecated // TODO remove/move ENG-13798
        @Nullable @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @Deprecated // TODO remove/move ENG-13798
        @Nullable @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @Nullable @JsonProperty(SUCCESS) Boolean success,
        @Nullable @JsonProperty(MESSAGE) String message) {
        this.partnerRewardId = partnerRewardId;
        this.costCode = costCode;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.success = success;
        this.message = message;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @Deprecated // TODO remove/move ENG-13798
    @Nullable
    @JsonProperty(COST_CODE)
    public String getCostCode() {
        return costCode;
    }

    @Deprecated // TODO remove/move ENG-13798
    @Nullable
    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @Deprecated // TODO remove/move ENG-13798
    @Nullable
    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
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
