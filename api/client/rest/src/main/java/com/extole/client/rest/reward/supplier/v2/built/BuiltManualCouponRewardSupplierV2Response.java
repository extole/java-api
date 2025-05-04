package com.extole.client.rest.reward.supplier.v2.built;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltManualCouponRewardSupplierV2Response extends BuiltBaseRewardSupplierV2Response {
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";
    private static final String DEFAULT_COUPON_EXPIRY_DATE = "default_coupon_expiry_date";
    private static final String MINIMUM_COUPON_LIFETIME = "minimum_coupon_lifetime";
    private static final String COUPON_COUNT_WARN_LIMIT = "coupon_count_warn_limit";

    private final Integer couponCountWarnLimit;
    private final ZonedDateTime defaultCouponExpiryDate;
    private final Long minimumCouponLifetime;

    public BuiltManualCouponRewardSupplierV2Response(@JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(DISPLAY_TYPE) String displayType,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) FaceValueAlgorithmType faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) BigDecimal cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) BigDecimal minCashBack,
        @JsonProperty(CASH_BACK_MAX) BigDecimal maxCashBack,
        @JsonProperty(LIMIT_PER_DAY) Optional<Integer> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) Optional<Integer> limitPerHour,
        @JsonProperty(NAME) String name,
        @JsonProperty(COUPON_COUNT_WARN_LIMIT) Integer couponCountWarnLimit,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(MINIMUM_COUPON_LIFETIME) Long minimumCouponLifetime,
        @JsonProperty(DEFAULT_COUPON_EXPIRY_DATE) ZonedDateTime defaultCouponExpiryDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(id, partnerRewardSupplierId, displayType, name, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, limitPerDay, limitPerHour, faceValueType, createdDate, updatedDate, componentIds,
            componentReferences, tags, data, enabled, stateTransitions);
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
