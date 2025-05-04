package com.extole.client.rest.reward.supplier.salesforce.built;

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

public class BuiltSalesforceCouponRewardSupplierResponse extends BuiltRewardSupplierResponse {

    public static final String SALESFORCE_REWARD_SUPPLIER_TYPE = "SALESFORCE_COUPON";

    private static final String COUPON_POOL_ID = "coupon_pool_id";
    private static final String BALANCE_REFILL_AMOUNT = "balance_refill_amount";
    private static final String INITIAL_OFFSET = "initial_offset";
    private static final String SETTINGS_ID = "settings_id";

    private final String couponPoolId;
    private final Integer balanceRefillAmount;
    private final Integer initialOffset;
    private final String settingsId;

    public BuiltSalesforceCouponRewardSupplierResponse(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
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
        @JsonProperty(COUPON_POOL_ID) String couponPoolId,
        @JsonProperty(BALANCE_REFILL_AMOUNT) Integer balanceRefillAmount,
        @JsonProperty(INITIAL_OFFSET) Integer initialOffset,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(SETTINGS_ID) String settingsId,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.SALESFORCE_COUPON, id, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            name, faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack, limitPerDay,
            limitPerHour, faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data,
            enabled, stateTransitions);
        this.couponPoolId = couponPoolId;
        this.balanceRefillAmount = balanceRefillAmount;
        this.initialOffset = initialOffset;
        this.settingsId = settingsId;
    }

    @JsonProperty(COUPON_POOL_ID)
    public String getCouponPoolId() {
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
