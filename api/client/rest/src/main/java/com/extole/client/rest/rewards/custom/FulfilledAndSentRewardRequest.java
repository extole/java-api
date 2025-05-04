package com.extole.client.rest.rewards.custom;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;

public class FulfilledAndSentRewardRequest {

    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String COST_CODE = "cost_code";
    private static final String PARTNER_REWARD_SENT_ID = "partner_reward_sent_id";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String MESSAGE = "message";
    private static final String EMAIL = "email";

    private final String partnerRewardId;
    private final String costCode;
    private final String partnerRewardSentId;
    @Deprecated // TBD - OPEN TICKET
    private final BigDecimal faceValue;
    @Deprecated // TBD - OPEN TICKET
    private final FaceValueType faceValueType;
    private final String message;
    private final String email;

    public FulfilledAndSentRewardRequest(@JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @Nullable @JsonProperty(COST_CODE) String costCode,
        @Nullable @JsonProperty(PARTNER_REWARD_SENT_ID) String partnerRewardSentId,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @Deprecated // TBD - OPEN TICKET
        @Nullable @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @Nullable @JsonProperty(MESSAGE) String message,
        @JsonProperty(EMAIL) String email) {
        this.partnerRewardId = partnerRewardId;
        this.costCode = costCode;
        this.partnerRewardSentId = partnerRewardSentId;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.message = message;
        this.email = email;
    }

    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @Nullable
    @JsonProperty(COST_CODE)
    public String getCostCode() {
        return costCode;
    }

    @JsonProperty(PARTNER_REWARD_SENT_ID)
    public String getPartnerRewardSentId() {
        return partnerRewardSentId;
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

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
