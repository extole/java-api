package com.extole.consumer.rest.me.reward;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.consumer.rest.me.JourneyKey;

public class RewardResponse {
    public static final String TAG_ADVOCATE = "advocate";
    public static final String TAG_FRIEND = "friend";

    @Deprecated // TBD - OPEN TICKET use reward_id
    private static final String ID = "id";
    private static final String REWARD_ID = "reward_id";
    private static final String AMOUNT = "amount";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String DATE_ISSUED = "date_issued";
    private static final String TYPE = "type";
    private static final String REWARD_TYPE = "reward_type";
    private static final String REWARD_CODE = "code";
    private static final String DATE_DELIVERED = "date_delivered";
    private static final String TAGS = "tags";

    private static final String STATE = "state";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String SANDBOX = "sandbox";
    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String FACE_VALUE = "face_value";
    private static final String DATE_EARNED = "date_earned";
    private static final String SLOTS = "slots";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String REWARD_NAME = "reward_name";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String REDEEMED_DATE = "redeemed_date";
    private static final String JOURNEY_NAME = "journey_name";
    private static final String JOURNEY_KEY = "journey_key";

    private final String rewardId;
    private final String amount;
    private final String faceValue;
    private final FaceValueType faceValueType;
    private final Type type;
    private final RewardType rewardType;
    private final String state;
    private final String campaignId;
    private final String programLabel;
    private final String sandbox;
    private final String rewardCode;
    private final String partnerRewardId;
    private final String dateEarned;
    private final String dateIssued;
    private final String dateDelivered;
    private final List<String> slots;
    private final List<String> tags;
    private final String rewardSupplierId;
    private final String partnerRewardSupplierId;
    private final String rewardName;
    private final String expiryDate;
    private final String redeemedDate;
    private final String journeyName;
    private final Optional<JourneyKey> journeyKey;

    public RewardResponse(@JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(STATE) String state,
        @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @JsonProperty(REWARD_CODE) String rewardCode,
        @JsonProperty(FACE_VALUE) String faceValue,
        @JsonProperty(AMOUNT) String amount,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(DATE_EARNED) String dateEarned,
        @JsonProperty(DATE_ISSUED) String dateIssued,
        @JsonProperty(DATE_DELIVERED) String dateDelivered,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(SANDBOX) String sandbox,
        @JsonProperty(SLOTS) List<String> slots,
        @JsonProperty(TAGS) List<String> tags,
        @JsonProperty(TYPE) Type type,
        @JsonProperty(REWARD_TYPE) RewardType rewardType,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) String partnerRewardSupplierId,
        @JsonProperty(REWARD_NAME) String rewardName,
        @JsonProperty(EXPIRY_DATE) String expiryDate,
        @JsonProperty(REDEEMED_DATE) String redeemedDate,
        @JsonProperty(JOURNEY_NAME) String journeyName,
        @JsonProperty(JOURNEY_KEY) Optional<JourneyKey> journeyKey) {
        this.state = state;
        this.rewardId = rewardId;
        this.amount = amount;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.dateIssued = dateIssued;
        this.type = type;
        this.rewardType = rewardType;
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.sandbox = sandbox;
        this.rewardCode = rewardCode;
        this.dateDelivered = dateDelivered;
        this.partnerRewardId = partnerRewardId;
        this.slots = slots;
        this.tags = tags;
        this.dateEarned = dateEarned;
        this.rewardSupplierId = rewardSupplierId;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.rewardName = rewardName;
        this.expiryDate = expiryDate;
        this.redeemedDate = redeemedDate;
        this.journeyName = journeyName;
        this.journeyKey = journeyKey;
    }

    @Deprecated // TBD - OPEN TICKET use reward_id
    @JsonProperty(ID)
    public String getId() {
        return rewardId;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @Deprecated // TBD - OPEN TICKET use FACE_VALUE
    @JsonProperty(AMOUNT)
    public String getAmount() {
        return amount;
    }

    @JsonProperty(FACE_VALUE)
    public String getFaceValue() {
        return faceValue;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @Deprecated // TBD - OPEN TICKET
    @JsonProperty(DATE_ISSUED)
    public String getDateIssued() {
        return dateIssued;
    }

    @JsonProperty(DATE_EARNED)
    public String getDateEarned() {
        return dateEarned;
    }

    @Deprecated // TODO remove in ENG-24516
    @Nullable
    @JsonProperty(TYPE)
    public Type getType() {
        return type;
    }

    @JsonProperty(REWARD_TYPE)
    public RewardType getRewardType() {
        return rewardType;
    }

    @Deprecated // TBD - OPEN TICKET use PARTNER_REWARD_ID
    @Nullable
    @JsonProperty(REWARD_CODE)
    public String getRewardCode() {
        return rewardCode;
    }

    @Deprecated // TBD - OPEN TICKET
    @Nullable
    @JsonProperty(DATE_DELIVERED)
    public String getDateDelivered() {
        return dateDelivered;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    public enum FaceValueType {
        PERCENT_OFF, POINTS, MONTH, USD, GBP, EUR, JPY, CNY, CAD, AUD, BRL, INR, NZD, MXN, KRW, TWD, TRY, HKD
    }

    @JsonProperty(SLOTS)
    public List<String> getSlots() {
        return slots;
    }

    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @JsonProperty(STATE)
    public String getState() {
        return state;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(SANDBOX)
    public String getSandbox() {
        return sandbox;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return this.rewardSupplierId;
    }

    @JsonProperty(REWARD_NAME)
    public String getRewardName() {
        return rewardName;
    }

    @JsonProperty(EXPIRY_DATE)
    public Optional<String> getExpiryDate() {
        return Optional.ofNullable(expiryDate);
    }

    @JsonProperty(REDEEMED_DATE)
    public Optional<String> getRedeemedDate() {
        return Optional.ofNullable(redeemedDate);
    }

    @JsonProperty(JOURNEY_NAME)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JOURNEY_KEY)
    public Optional<JourneyKey> getJourneyKey() {
        return journeyKey;
    }

    @Deprecated // TODO remove in ENG-24516
    public enum Type {
        TANGO_V2, COUPON, CUSTOM_REWARD, PAYPAL_PAYOUTS
    }

    public enum RewardType {
        ID, COUPON, LINK
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
