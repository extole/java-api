package com.extole.client.rest.rewards;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class RedeemedRewardStateResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String PARTNER_REWARD_REDEEM_ID = "partner_reward_redeem_id";
    private static final String MESSAGE = "message";
    private static final String OPERATOR_USER_ID = "operator_user_id";
    private static final String EVENT_NAME = "event_name";
    private static final String PARTNER_EVENT_ID = "partner_event_id";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String CREATED_AT = "created_at";

    private final String rewardId;
    private final String partnerRewardRedeemId;
    private final String message;
    private final String operatorUserId;
    private final String eventName;
    private final String partnerEventId;
    private final String causeEventId;
    private final ZonedDateTime createdAt;

    public RedeemedRewardStateResponse(@JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(PARTNER_REWARD_REDEEM_ID) String partnerRewardRedeemId,
        @JsonProperty(MESSAGE) String message,
        @Nullable @JsonProperty(OPERATOR_USER_ID) String operatorUserId,
        @Nullable @JsonProperty(EVENT_NAME) String eventName,
        @Nullable @JsonProperty(PARTNER_EVENT_ID) String partnerEventId,
        @Nullable @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt) {
        this.rewardId = rewardId;
        this.partnerRewardRedeemId = partnerRewardRedeemId;
        this.message = message;
        this.operatorUserId = operatorUserId;
        this.eventName = eventName;
        this.partnerEventId = partnerEventId;
        this.createdAt = createdAt;
        this.causeEventId = causeEventId;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(PARTNER_REWARD_REDEEM_ID)
    public String getPartnerRewardRedeemId() {
        return partnerRewardRedeemId;
    }

    @Nullable
    @JsonProperty(MESSAGE)
    public String getMessage() {
        return message;
    }

    @JsonProperty(OPERATOR_USER_ID)
    @Nullable
    public String getOperatorUserId() {
        return operatorUserId;
    }

    @JsonProperty(EVENT_NAME)
    @Nullable
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(PARTNER_EVENT_ID)
    @Nullable
    public String getPartnerEventId() {
        return partnerEventId;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
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
