package com.extole.client.rest.person.v2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.extole.common.lang.ToString;

public class PersonRewardV2Response {
    private static final String ID = "id";
    private static final String REWARD_ID = "reward_id";
    private static final String AMOUNT = "amount";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String DATE_ISSUED = "date_issued";

    private static final String STATE = "state";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String SANDBOX = "sandbox";
    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String FACE_VALUE = "face_value";
    private static final String DATE_EARNED = "date_earned";
    private static final String SLOTS = "slots";
    private static final String TAGS = "tags";
    private static final String REWARD_NAME = "reward_name";
    private static final String VALUE_OF_REWARDED_EVENT = "value_of_rewarded_event";
    private static final String DATA = "data";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String REDEEMED_DATE = "redeemed_date";

    private final String id;
    private final String rewardId;
    private final String rewardSupplierId;
    private final String amount;
    private final String faceValue;
    private final String faceValueType;
    private final ZonedDateTime dateIssued;

    private final String state;
    private final String campaignId;
    private final String programLabel;
    private final String sandbox;
    private final String partnerRewardId;
    private final ZonedDateTime dateEarned;
    private final List<String> tags;
    private final String partnerRewardSupplierId;
    private final String rewardName;
    private final BigDecimal valueOfRewardedEvent;
    private final Map<String, String> data;
    private final ZonedDateTime expiryDate;
    private final Optional<ZonedDateTime> redeemedDate;

    @JsonCreator
    public PersonRewardV2Response(@JsonProperty(ID) String id,
        @JsonProperty(REWARD_ID) String rewardId,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) String partnerRewardSupplierId,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(AMOUNT) String amount,
        @JsonProperty(FACE_VALUE) String faceValue,
        @JsonProperty(FACE_VALUE_TYPE) String faceValueType,
        @JsonProperty(DATE_ISSUED) ZonedDateTime dateIssued,
        @JsonProperty(STATE) String state,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(SANDBOX) String sandbox,
        @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @JsonProperty(DATE_EARNED) ZonedDateTime dateEarned,
        @JsonProperty(SLOTS) List<String> slots,
        @JsonProperty(TAGS) List<String> tags,
        @JsonProperty(REWARD_NAME) String rewardName,
        @JsonProperty(VALUE_OF_REWARDED_EVENT) BigDecimal valueOfRewardedEvent,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(EXPIRY_DATE) ZonedDateTime expiryDate,
        @JsonProperty(REDEEMED_DATE) Optional<ZonedDateTime> redeemedDate) {
        this.id = id;
        this.rewardId = rewardId;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.rewardSupplierId = rewardSupplierId;
        this.amount = amount;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.dateIssued = dateIssued;
        this.state = state;
        this.campaignId = campaignId;
        this.programLabel = programLabel;
        this.sandbox = sandbox;
        this.partnerRewardId = partnerRewardId;
        this.tags = tags != null ? ImmutableList.copyOf(tags) : ImmutableList.copyOf(slots);
        this.dateEarned = dateEarned;
        this.rewardName = rewardName;
        this.valueOfRewardedEvent = valueOfRewardedEvent.setScale(2, RoundingMode.HALF_UP);
        this.data = ImmutableMap.copyOf(data);
        this.expiryDate = expiryDate;
        this.redeemedDate = redeemedDate;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(REWARD_ID)
    public String getRewardId() {
        return rewardId;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @Deprecated // TBD - OPEN TICKET
    @JsonProperty(AMOUNT)
    public String getAmount() {
        return amount;
    }

    @JsonProperty(FACE_VALUE)
    public String getFaceValue() {
        return faceValue;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public String getFaceValueType() {
        return faceValueType;
    }

    @Deprecated // TBD - OPEN TICKET
    @JsonProperty(DATE_ISSUED)
    public ZonedDateTime getDateIssued() {
        return dateIssued;
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

    @JsonProperty(DATE_EARNED)
    public ZonedDateTime getDateEarned() {
        return dateEarned;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @Deprecated // TODO remove after UI switch ENG-15542
    @JsonProperty(SLOTS)
    public List<String> getSlots() {
        return tags;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty(REWARD_NAME)
    public String getRewardName() {
        return rewardName;
    }

    @JsonProperty(VALUE_OF_REWARDED_EVENT)
    public BigDecimal getValueOfRewardedEvent() {
        return valueOfRewardedEvent;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(EXPIRY_DATE)
    public Optional<ZonedDateTime> getExpiryDate() {
        return Optional.ofNullable(expiryDate);
    }

    @JsonProperty(REDEEMED_DATE)
    public Optional<ZonedDateTime> getRedeemedDate() {
        return redeemedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
