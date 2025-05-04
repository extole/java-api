package com.extole.client.rest.reward.supplier.paypal.built;

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
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.built.BuiltRewardSupplierResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltPayPalPayoutsRewardSupplierResponse extends BuiltRewardSupplierResponse {

    public static final String PAYPAL_REWARD_SUPPLIER_TYPE = "PAYPAL_PAYOUTS";

    private static final String MERCHANT_TOKEN = "merchant_token";
    protected static final String DESCRIPTION = "description";

    private final String merchantToken;
    private final Optional<String> description;

    public BuiltPayPalPayoutsRewardSupplierResponse(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(MERCHANT_TOKEN) String merchantToken,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) String displayType,
        @JsonProperty(NAME) String name,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) FaceValueAlgorithmType faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) BigDecimal cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) BigDecimal minCashBack,
        @JsonProperty(CASH_BACK_MAX) BigDecimal maxCashBack,
        @JsonProperty(LIMIT_PER_DAY) Optional<Integer> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) Optional<Integer> limitPerHour,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.PAYPAL_PAYOUTS, id, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            name, faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack, limitPerDay,
            limitPerHour, faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data,
            enabled, stateTransitions);
        this.merchantToken = merchantToken;
        this.description = description;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
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
