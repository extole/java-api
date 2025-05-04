package com.extole.client.rest.reward.supplier.v2;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class SalesforceCouponRewardSupplierV2Response extends BaseRewardSupplierV2Response {

    private static final String COUPON_POOL_ID = "coupon_pool_id";
    private static final String BALANCE_REFILL_AMOUNT = "balance_refill_amount";
    private static final String INITIAL_OFFSET = "initial_offset";
    private static final String SETTINGS_ID = "settings_id";

    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> couponPoolId;
    private final Integer balanceRefillAmount;
    private final Integer initialOffset;
    private final String settingsId;

    public SalesforceCouponRewardSupplierV2Response(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(DISPLAY_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType,
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
        @JsonProperty(NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name,
        @JsonProperty(COUPON_POOL_ID) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> couponPoolId,
        @JsonProperty(BALANCE_REFILL_AMOUNT) Integer balanceRefillAmount,
        @JsonProperty(INITIAL_OFFSET) Integer initialOffset,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(SETTINGS_ID) String settingsId,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(id, partnerRewardSupplierId, displayType, name, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, limitPerDay, limitPerHour, faceValueType, createdDate, updatedDate, componentIds,
            componentReferences, tags, data, enabled, stateTransitions);
        this.couponPoolId = couponPoolId;
        this.balanceRefillAmount = balanceRefillAmount;
        this.initialOffset = initialOffset;
        this.settingsId = settingsId;
    }

    @JsonProperty(COUPON_POOL_ID)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getCouponPoolId() {
        return couponPoolId;
    }

    @JsonProperty(BALANCE_REFILL_AMOUNT)
    public Integer getBalanceRefillAmount() {
        return balanceRefillAmount;
    }

    @JsonProperty(INITIAL_OFFSET)
    public Integer getInitialOffset() {
        return initialOffset;
    }

    @JsonProperty(SETTINGS_ID)
    public String getSettingsId() {
        return settingsId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
