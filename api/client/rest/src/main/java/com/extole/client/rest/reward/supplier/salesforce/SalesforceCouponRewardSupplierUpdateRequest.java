package com.extole.client.rest.reward.supplier.salesforce;

import java.math.BigDecimal;
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

public class SalesforceCouponRewardSupplierUpdateRequest extends RewardSupplierUpdateRequest {

    public static final String SALESFORCE_REWARD_SUPPLIER_TYPE = "SALESFORCE_COUPON";

    private static final String BALANCE_REFILL_AMOUNT = "balance_refill_amount";

    private final Omissible<Integer> balanceRefillAmount;

    public SalesforceCouponRewardSupplierUpdateRequest(
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
        @JsonProperty(BALANCE_REFILL_AMOUNT) Omissible<Integer> balanceRefillAmount,
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
        super(RewardSupplierType.SALESFORCE_COUPON, name, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, faceValueType, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            description, limitPerDay, limitPerHour, componentIds, componentReferences, tags, data, enabled);
        this.balanceRefillAmount = balanceRefillAmount;
    }

    @JsonProperty(BALANCE_REFILL_AMOUNT)
    public Omissible<Integer> getBalanceRefillAmount() {
        return balanceRefillAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends RewardSupplierUpdateRequest.Builder<SalesforceCouponRewardSupplierUpdateRequest, Builder> {
        private Omissible<Integer> balanceRefillAmount = Omissible.omitted();

        private Builder() {
        }

        public Builder withBalanceRefillAmount(Integer balanceRefillAmount) {
            this.balanceRefillAmount = Omissible.of(balanceRefillAmount);
            return this;
        }

        public SalesforceCouponRewardSupplierUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new SalesforceCouponRewardSupplierUpdateRequest(
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
                balanceRefillAmount,
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
