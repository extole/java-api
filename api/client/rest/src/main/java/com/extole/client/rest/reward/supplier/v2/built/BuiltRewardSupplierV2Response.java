package com.extole.client.rest.reward.supplier.v2.built;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;

public class BuiltRewardSupplierV2Response {
    private static final String REWARD_SUPPLIER_ID = "id";
    private static final String REWARD_SUPPLIER_TYPE = "reward_type";
    private static final String FACE_VALUE_ALGORITHM_TYPE = "face_value_algorithm_type";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String CASH_BACK_PERCENTAGE = "cash_back_percentage";
    private static final String CASH_BACK_MIN = "cash_back_min";
    private static final String CASH_BACK_MAX = "cash_back_max";
    private static final String LIMIT_PER_DAY = "limit_per_day";
    private static final String LIMIT_PER_HOUR = "limit_per_hour";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String DISPLAY_TYPE = "display_type";
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String ENABLED = "enabled";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String id;
    private final RewardSupplierType rewardSupplierType;
    private final FaceValueType faceValueType;
    private final FaceValueAlgorithmType faceValueAlgorithmType;
    private final BigDecimal faceValue;
    private final BigDecimal cashBackPercentage;
    private final BigDecimal minCashBack;
    private final BigDecimal maxCashBack;
    private final Optional<Integer> limitPerDay;
    private final Optional<Integer> limitPerHour;
    private final Optional<String> partnerRewardSupplierId;
    private final String displayType;
    private final String name;
    private final Set<String> tags;
    private final Map<String, String> data;
    private final Boolean enabled;
    private final Optional<String> description;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public BuiltRewardSupplierV2Response(@JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(REWARD_SUPPLIER_TYPE) RewardSupplierType rewardType,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) FaceValueAlgorithmType faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) BigDecimal cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) BigDecimal minCashBack,
        @JsonProperty(CASH_BACK_MAX) BigDecimal maxCashBack,
        @JsonProperty(LIMIT_PER_DAY) Optional<Integer> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) Optional<Integer> limitPerHour,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(DISPLAY_TYPE) String displayType,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(ENABLED) Boolean enabled) {
        this.id = id;
        this.rewardSupplierType = rewardType;
        this.faceValueType = faceValueType;
        this.faceValueAlgorithmType = faceValueAlgorithmType;
        this.faceValue = faceValue;
        this.cashBackPercentage = cashBackPercentage;
        this.minCashBack = minCashBack;
        this.maxCashBack = maxCashBack;
        this.limitPerDay = limitPerDay;
        this.limitPerHour = limitPerHour;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.displayType = displayType;
        this.name = name;
        this.tags = ImmutableSet.copyOf(tags);
        this.data = ImmutableMap.copyOf(data);
        this.enabled = enabled;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return id;
    }

    @JsonProperty(REWARD_SUPPLIER_TYPE)
    public RewardSupplierType getRewardSupplierType() {
        return rewardSupplierType;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public FaceValueAlgorithmType getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @JsonProperty(CASH_BACK_PERCENTAGE)
    public BigDecimal getCashBackPercentage() {
        return cashBackPercentage;
    }

    @JsonProperty(CASH_BACK_MIN)
    public BigDecimal getMinCashBack() {
        return minCashBack;
    }

    @JsonProperty(CASH_BACK_MAX)
    public BigDecimal getMaxCashBack() {
        return maxCashBack;
    }

    @JsonProperty(LIMIT_PER_DAY)
    public Optional<Integer> getLimitPerDay() {
        return limitPerDay;
    }

    @JsonProperty(LIMIT_PER_HOUR)
    public Optional<Integer> getLimitPerHour() {
        return limitPerHour;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public Optional<String> getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(DISPLAY_TYPE)
    public String getDisplayType() {
        return displayType;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
