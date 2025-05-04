package com.extole.client.rest.reward.supplier.manual;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.RewardSupplierUpdateRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class ManualCouponRewardSupplierUpdateRequest extends RewardSupplierUpdateRequest {

    public static final String MANUAL_REWARD_SUPPLIER_TYPE = "MANUAL_COUPON";

    private static final String COUPON_COUNT_WARN_LIMIT = "coupon_count_warn_limit";
    private static final String MINIMUM_COUPON_LIFETIME = "minimum_coupon_lifetime";
    private static final String DEFAULT_COUPON_EXPIRY_DATE = "default_coupon_expiry_date";

    private final Omissible<Integer> couponCountWarnLimit;
    private final Omissible<Long> minimumCouponLifetime;
    private final Omissible<ZonedDateTime> defaultCouponExpiryDate;

    public ManualCouponRewardSupplierUpdateRequest(
        @JsonProperty(FACE_VALUE_TYPE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType>> faceValueType,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack,
        @JsonProperty(CASH_BACK_MAX) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Omissible<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) Omissible<PartnerRewardKeyType> partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType,
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name,
        @JsonProperty(COUPON_COUNT_WARN_LIMIT) Omissible<Integer> couponCountWarnLimit,
        @JsonProperty(MINIMUM_COUPON_LIFETIME) Omissible<Long> minimumCouponLifetime,
        @JsonProperty(DEFAULT_COUPON_EXPIRY_DATE) Omissible<ZonedDateTime> defaultCouponExpiryDate,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description,
        @JsonProperty(LIMIT_PER_DAY) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(DATA) Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled) {
        super(RewardSupplierType.MANUAL_COUPON, name, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, faceValueType, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            description, limitPerDay, limitPerHour, componentIds, componentReferences, tags, data, enabled);
        this.couponCountWarnLimit = couponCountWarnLimit;
        this.minimumCouponLifetime = minimumCouponLifetime;
        this.defaultCouponExpiryDate = defaultCouponExpiryDate;
    }

    @JsonProperty(COUPON_COUNT_WARN_LIMIT)
    public Omissible<Integer> getCouponCountWarnLimit() {
        return couponCountWarnLimit;
    }

    @JsonProperty(MINIMUM_COUPON_LIFETIME)
    public Omissible<Long> getMinimumCouponLifetime() {
        return minimumCouponLifetime;
    }

    @JsonProperty(DEFAULT_COUPON_EXPIRY_DATE)
    public Omissible<ZonedDateTime> getDefaultCouponExpiryDate() {
        return defaultCouponExpiryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends RewardSupplierUpdateRequest.Builder<ManualCouponRewardSupplierUpdateRequest, Builder> {
        private Omissible<Integer> couponCountWarnLimit = Omissible.omitted();
        private Omissible<Long> minimumCouponLifetime = Omissible.omitted();
        private Omissible<ZonedDateTime> defaultCouponExpiryDate = Omissible.omitted();

        private Builder() {
        }

        public Builder withCouponCountWarnLimit(Integer couponCountWarnLimit) {
            this.couponCountWarnLimit = Omissible.of(couponCountWarnLimit);
            return this;
        }

        public Builder withMinimumCouponLifetime(Long minimumCouponLifetime) {
            this.minimumCouponLifetime = Omissible.of(minimumCouponLifetime);
            return this;
        }

        public Builder withDefaultCouponExpiryDate(ZonedDateTime defaultCouponExpiryDate) {
            this.defaultCouponExpiryDate = Omissible.of(defaultCouponExpiryDate);
            return this;
        }

        public ManualCouponRewardSupplierUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new ManualCouponRewardSupplierUpdateRequest(
                faceValueType,
                faceValueAlgorithmType,
                faceValue,
                cashBackPercentage,
                minCashBack,
                maxCashBack,
                partnerRewardSupplierId,
                partnerRewardKeyType,
                displayType,
                name,
                couponCountWarnLimit,
                minimumCouponLifetime,
                defaultCouponExpiryDate,
                description,
                limitPerDay,
                limitPerHour,
                componentIds,
                componentReferences,
                tags,
                data,
                enabled);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
