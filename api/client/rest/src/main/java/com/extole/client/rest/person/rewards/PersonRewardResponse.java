package com.extole.client.rest.person.rewards;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.extole.client.rest.person.JourneyKey;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.common.lang.ToString;

public class PersonRewardResponse {

    private static final String ID = "id";
    private static final String PROGRAM = "program";
    private static final String CAMPAIGN_ID = "campaign_id";
    private static final String CONTAINER = "container";
    private static final String SANDBOX = "sandbox";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String PARTNER_REWARD_ID = "partner_reward_id";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String NAME = "name";
    private static final String VALUE_OF_REWARDED_EVENT = "value_of_rewarded_event";
    private static final String STATE = "state";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String EARNED_DATE = "earned_date";
    private static final String REDEEMED_DATE = "redeemed_date";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String JOURNEY_NAME = "journey_name";
    private static final String JOURNEY_KEY = "journey_key";

    private final String id;
    private final String program;
    private final String campaignId;
    private final String container;
    private final String sandbox;
    private final String rewardSupplierId;
    private final BigDecimal faceValue;
    private final FaceValueType faceValueType;
    private final String partnerRewardId;
    private final String partnerRewardSupplierId;
    private final String name;
    private final BigDecimal valueOfRewardedEvent;
    private final RewardState state;
    private final List<String> tags;
    private final Map<String, PersonRewardDataResponse> data;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final ZonedDateTime earnedDate;
    private final ZonedDateTime redeemedDate;
    private final ZonedDateTime expiryDate;
    private final String journeyName;
    private final Optional<JourneyKey> journeyKey;

    @JsonCreator
    public PersonRewardResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(PROGRAM) String program,
        @JsonProperty(CAMPAIGN_ID) String campaignId,
        @JsonProperty(CONTAINER) String container,
        @JsonProperty(SANDBOX) String sandbox,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(PARTNER_REWARD_ID) String partnerRewardId,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) String partnerRewardSupplierId,
        @JsonProperty(NAME) String name,
        @JsonProperty(VALUE_OF_REWARDED_EVENT) BigDecimal valueOfRewardedEvent,
        @JsonProperty(STATE) RewardState state,
        @JsonProperty(TAGS) List<String> tags,
        @JsonProperty(DATA) Map<String, PersonRewardDataResponse> data,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(EARNED_DATE) ZonedDateTime earnedDate,
        @JsonProperty(REDEEMED_DATE) ZonedDateTime redeemedDate,
        @JsonProperty(EXPIRY_DATE) ZonedDateTime expiryDate,
        @JsonProperty(JOURNEY_NAME) String journeyName,
        @JsonProperty(JOURNEY_KEY) Optional<JourneyKey> journeyKey) {
        this.id = id;
        this.program = program;
        this.campaignId = campaignId;
        this.container = container;
        this.sandbox = sandbox;
        this.rewardSupplierId = rewardSupplierId;
        this.faceValue = faceValue;
        this.faceValueType = faceValueType;
        this.partnerRewardId = partnerRewardId;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.name = name;
        this.valueOfRewardedEvent = valueOfRewardedEvent;
        this.state = state;
        this.tags = tags == null ? ImmutableList.of() : ImmutableList.copyOf(tags);
        this.data = data == null ? ImmutableMap.of() : ImmutableMap.copyOf(data);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.earnedDate = earnedDate;
        this.redeemedDate = redeemedDate;
        this.expiryDate = expiryDate;
        this.journeyName = journeyName;
        this.journeyKey = journeyKey;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(PROGRAM)
    public String getProgram() {
        return program;
    }

    @JsonProperty(CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(CONTAINER)
    public String getContainer() {
        return container;
    }

    @JsonProperty(SANDBOX)
    public String getSandbox() {
        return sandbox;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(PARTNER_REWARD_ID)
    public String getPartnerRewardId() {
        return partnerRewardId;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public String getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(VALUE_OF_REWARDED_EVENT)
    public BigDecimal getValueOfRewardedEvent() {
        return valueOfRewardedEvent;
    }

    @JsonProperty(STATE)
    public RewardState getState() {
        return state;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<String, PersonRewardDataResponse> getData() {
        return data;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(EARNED_DATE)
    public ZonedDateTime getEarnedDate() {
        return earnedDate;
    }

    @JsonProperty(REDEEMED_DATE)
    public ZonedDateTime getRedeemedDate() {
        return redeemedDate;
    }

    @JsonProperty(EXPIRY_DATE)
    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    @JsonProperty(JOURNEY_NAME)
    public String getJourneyName() {
        return journeyName;
    }

    @JsonProperty(JOURNEY_KEY)
    public Optional<JourneyKey> getJourneyKey() {
        return journeyKey;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new PersonRewardResponse.Builder();
    }

    public static final class Builder {

        private String id;
        private String program;
        private String campaignId;
        private String container;
        private String sandbox;
        private String rewardSupplierId;
        private BigDecimal faceValue;
        private FaceValueType faceValueType;
        private String partnerRewardId;
        private String partnerRewardSupplierId;
        private String name;
        private BigDecimal valueOfRewardedEvent;
        private RewardState state;
        private List<String> tags;
        private Map<String, PersonRewardDataResponse> data;
        private ZonedDateTime createdDate;
        private ZonedDateTime updatedDate;
        private ZonedDateTime earnedDate;
        private ZonedDateTime redeemedDate;
        private ZonedDateTime expiryDate;
        private String journeyName;
        private Optional<JourneyKey> journeyKey = Optional.empty();

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withProgram(String program) {
            this.program = program;
            return this;
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder withContainer(String container) {
            this.container = container;
            return this;
        }

        public Builder withSandbox(String sandbox) {
            this.sandbox = sandbox;
            return this;
        }

        public Builder withRewardSupplierId(String rewardSupplierId) {
            this.rewardSupplierId = rewardSupplierId;
            return this;
        }

        public Builder withFaceValue(BigDecimal faceValue) {
            this.faceValue = faceValue;
            return this;
        }

        public Builder withFaceValueType(FaceValueType faceValueType) {
            this.faceValueType = faceValueType;
            return this;
        }

        public Builder withPartnerRewardId(String partnerRewardId) {
            this.partnerRewardId = partnerRewardId;
            return this;
        }

        public Builder withPartnerRewardSupplierId(String partnerRewardSupplierId) {
            this.partnerRewardSupplierId = partnerRewardSupplierId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withValueOfRewardedEvent(BigDecimal valueOfRewardedEvent) {
            this.valueOfRewardedEvent = valueOfRewardedEvent;
            return this;
        }

        public Builder withState(RewardState state) {
            this.state = state;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withData(Map<String, PersonRewardDataResponse> data) {
            this.data = data;
            return this;
        }

        public Builder withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder withEarnedDate(ZonedDateTime earnedDate) {
            this.earnedDate = earnedDate;
            return this;
        }

        public Builder withRedeemedDate(ZonedDateTime redeemedDate) {
            this.redeemedDate = redeemedDate;
            return this;
        }

        public Builder withExpiryDate(ZonedDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder withJourneyName(String journeyName) {
            this.journeyName = journeyName;
            return this;
        }

        public Builder withJourneyKey(JourneyKey journeyKey) {
            this.journeyKey = Optional.of(journeyKey);
            return this;
        }

        public PersonRewardResponse build() {
            return new PersonRewardResponse(id, program, campaignId, container, sandbox, rewardSupplierId, faceValue,
                faceValueType, partnerRewardId, partnerRewardSupplierId, name, valueOfRewardedEvent, state, tags, data,
                createdDate, updatedDate, earnedDate, redeemedDate, expiryDate, journeyName, journeyKey);
        }

    }
}
