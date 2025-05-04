package com.extole.client.rest.rewards;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.common.lang.ToString;

public class RewardResponse {

    private static final String REWARD_ID = "reward_id";
    private static final String CAUSE_EVENT_ID = "cause_event_id";
    private static final String ROOT_EVENT_ID = "root_event_id";
    private static final String ACTION_ID = "action_id";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String PARTNER_REWARD_KEY_TYPE = "partner_reward_key_type";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String PERSON_ID = "person_id";
    private static final String PARTNER_USER_ID = "partner_user_id";
    private static final String EMAIL = "email";
    private static final String STATE = "state";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String CREATED_AT = "created_at";
    private static final String DATA = "data";
    private static final String JOURNEY_NAME = "journey_name";
    private static final String JOURNEY_KEY = "journey_key";
    private static final String SANDBOX = "sandbox";
    private static final String CONTAINER = "container";

    private final String rewardId;
    private final String causeEventId;
    private final String rootEventId;
    private final String actionId;
    private final String rewardSupplierId;
    private final String partnerRewardSupplierId;
    private final PartnerRewardKeyType partnerRewardKeyType;
    private final String campaignId;
    private final String personId;
    private final String partnerUserId;
    private final String email;
    private final RewardStateType state;
    private final BigDecimal faceValue;
    private final FaceValueType faceValueType;
    private final String partnerRewardId;
    private final ZonedDateTime createdAt;
    private final Map<String, String> data;
    private final String journeyName;
    private final Optional<JourneyKey> journeyKey;
    private final String sandbox;
    private final String container;

    public RewardResponse(
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(CAUSE_EVENT_ID) String causeEventId,
        @JsonProperty(ROOT_EVENT_ID) String rootEventId,
        @JsonProperty(ACTION_ID) String actionId,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @Nullable @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) String partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(PERSON_ID) String personId,
        @Nullable @JsonProperty(PARTNER_USER_ID) String partnerUserId,
        @Nullable @JsonProperty(EMAIL) String email,
        @JsonProperty(STATE) RewardStateType state,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @Nullable @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @JsonProperty(CREATED_AT) ZonedDateTime createdAt,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(JOURNEY_NAME) String journeyName,
        @JsonProperty(JOURNEY_KEY) Optional<JourneyKey> journeyKey,
        @JsonProperty(SANDBOX) String sandbox,
        @JsonProperty(CONTAINER) String container) {
        this.rewardId = rewardId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.actionId = actionId;
        this.rewardSupplierId = rewardSupplierId;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.campaignId = campaignId;
        this.personId = personId;
        this.partnerUserId = partnerUserId;
        this.email = email;
        this.state = state;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.partnerRewardId = partnerRewardId;
        this.createdAt = createdAt;
        this.data = Maps.newHashMap(data);
        this.journeyName = journeyName;
        this.journeyKey = journeyKey;
        this.sandbox = sandbox;
        this.container = container;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(CAUSE_EVENT_ID)
    public String getCauseEventId() {
        return causeEventId;
    }

    @JsonProperty(ROOT_EVENT_ID)
    public String getRootEventId() {
        return rootEventId;
    }

    @JsonProperty(ACTION_ID)
    public String getActionId() {
        return actionId;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(PARTNER_REWARD_KEY_TYPE)
    public PartnerRewardKeyType getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(PERSON_ID)
    public String getPersonId() {
        return personId;
    }

    @Nullable
    @JsonProperty(PARTNER_USER_ID)
    public String getPartnerUserId() {
        return partnerUserId;
    }

    @Nullable
    @JsonProperty(EMAIL)
    public String getEmail() {
        return email;
    }

    @JsonProperty(STATE)
    public RewardStateType getState() {
        return state;
    }

    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @Nullable
    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @JsonProperty(CREATED_AT)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(JOURNEY_NAME)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JOURNEY_KEY)
    public Optional<JourneyKey> getJourneyKey() {
        return journeyKey;
    }

    @JsonProperty(SANDBOX)
    public String getSandbox() {
        return sandbox;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
