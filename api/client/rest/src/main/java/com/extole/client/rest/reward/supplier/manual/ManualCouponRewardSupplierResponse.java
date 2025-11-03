package com.extole.client.rest.reward.supplier.manual;

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
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierResponse;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class ManualCouponRewardSupplierResponse extends RewardSupplierResponse {

    public static final String MANUAL_REWARD_SUPPLIER_TYPE = "MANUAL_COUPON";

    private static final String DEFAULT_COUPON_EXPIRY_DATE = "default_coupon_expiry_date";
    private static final String MINIMUM_COUPON_LIFETIME = "minimum_coupon_lifetime";
    private static final String COUPON_COUNT_WARN_LIMIT = "coupon_count_warn_limit";

    private final Integer couponCountWarnLimit;
    private final ZonedDateTime defaultCouponExpiryDate;
    private final Long minimumCouponLifetime;

    public ManualCouponRewardSupplierResponse(@JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
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
        @JsonProperty(DISPLAY_NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName,
        @JsonProperty(COUPON_COUNT_WARN_LIMIT) Integer couponCountWarnLimit,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(MINIMUM_COUPON_LIFETIME) Long minimumCouponLifetime,
        @JsonProperty(DEFAULT_COUPON_EXPIRY_DATE) ZonedDateTime defaultCouponExpiryDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.MANUAL_COUPON, id, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            name, displayName, faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack,
            limitPerDay,
            limitPerHour, faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data,
            enabled, stateTransitions);
        this.couponCountWarnLimit = couponCountWarnLimit;
        this.minimumCouponLifetime = minimumCouponLifetime;
        this.defaultCouponExpiryDate = defaultCouponExpiryDate;
    }

    @JsonProperty(COUPON_COUNT_WARN_LIMIT)
    public Integer getCouponCountWarnLimit() {
        return couponCountWarnLimit;
    }

    @JsonProperty(MINIMUM_COUPON_LIFETIME)
    public Long getMinimumCouponLifetime() {
        return minimumCouponLifetime;
    }

    @JsonProperty(DEFAULT_COUPON_EXPIRY_DATE)
    public Optional<ZonedDateTime> getDefaultCouponExpiryDate() {
        return Optional.ofNullable(defaultCouponExpiryDate);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
