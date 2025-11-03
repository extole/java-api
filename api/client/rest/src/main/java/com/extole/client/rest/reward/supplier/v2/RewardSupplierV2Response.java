package com.extole.client.rest.reward.supplier.v2;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class RewardSupplierV2Response {
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
    private static final String DISPLAY_NAME = "display_name";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String ENABLED = "enabled";
    private static final String STATE_TRANSITIONS = "state_transitions";

    private final String id;
    private final RewardSupplierType rewardSupplierType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour;
    private final Optional<String> partnerRewardSupplierId;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Set<String> tags;
    private final Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled;
    private final Map<RewardState, List<RewardState>> stateTransitions;

    public RewardSupplierV2Response(@JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(REWARD_SUPPLIER_TYPE) RewardSupplierType rewardType,
        @JsonProperty(FACE_VALUE_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            FaceValueType> faceValueType,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            FaceValueAlgorithmType> faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            BigDecimal> cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack,
        @JsonProperty(CASH_BACK_MAX) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack,
        @JsonProperty(LIMIT_PER_DAY) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            Optional<Integer>> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            Optional<Integer>> limitPerHour,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(DISPLAY_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType,
        @JsonProperty(NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name,
        @JsonProperty(DISPLAY_NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
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
        this.displayName = displayName;
        this.description = description;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.tags = tags;
        this.data = data;
        this.enabled = enabled;
        this.stateTransitions = stateTransitions;
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
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return faceValue;
    }

    @JsonProperty(CASH_BACK_PERCENTAGE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return cashBackPercentage;
    }

    @JsonProperty(CASH_BACK_MIN)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return minCashBack;
    }

    @JsonProperty(CASH_BACK_MAX)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return maxCashBack;
    }

    @JsonProperty(LIMIT_PER_DAY)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> getLimitPerDay() {
        return limitPerDay;
    }

    @JsonProperty(LIMIT_PER_HOUR)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> getLimitPerHour() {
        return limitPerHour;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public Optional<String> getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(DISPLAY_TYPE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDisplayType() {
        return displayType;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(DESCRIPTION)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDescription() {
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

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(ENABLED)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(DATA)
    public Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getData() {
        return data;
    }

    @JsonProperty(STATE_TRANSITIONS)
    public Map<RewardState, List<RewardState>> getStateTransitions() {
        return stateTransitions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
