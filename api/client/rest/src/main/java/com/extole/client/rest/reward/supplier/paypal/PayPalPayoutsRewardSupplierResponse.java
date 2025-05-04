package com.extole.client.rest.reward.supplier.paypal;

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

public class PayPalPayoutsRewardSupplierResponse extends RewardSupplierResponse {

    public static final String PAYPAL_REWARD_SUPPLIER_TYPE = "PAYPAL_PAYOUTS";

    private static final String MERCHANT_TOKEN = "merchant_token";
    protected static final String DESCRIPTION = "description";

    private final String merchantToken;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description;

    public PayPalPayoutsRewardSupplierResponse(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(MERCHANT_TOKEN) String merchantToken,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType,
        @JsonProperty(NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name,
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
        @JsonProperty(FACE_VALUE_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            FaceValueType> faceValueType,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.PAYPAL_PAYOUTS, id, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            name, faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack, limitPerDay,
            limitPerHour, faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data,
            enabled, stateTransitions);
        this.merchantToken = merchantToken;
        this.description = description;

    }

    @JsonProperty(DESCRIPTION)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(MERCHANT_TOKEN)
    public String getMerchantToken() {
        return merchantToken;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
