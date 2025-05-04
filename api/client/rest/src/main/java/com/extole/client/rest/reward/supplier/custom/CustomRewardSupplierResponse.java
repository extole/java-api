package com.extole.client.rest.reward.supplier.custom;

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
import com.extole.client.rest.reward.supplier.CustomRewardType;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierResponse;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CustomRewardSupplierResponse extends RewardSupplierResponse {

    public static final String CUSTOM_REWARD_SUPPLIER_TYPE = "CUSTOM_REWARD";

    private static final String TYPE = "type";
    private static final String REWARD_EMAIL_AUTO_SEND_ENABLED = "reward_email_auto_send_enabled";
    private static final String AUTO_FULFILLMENT_ENABLED = "auto_fulfillment_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_ENABLED = "missing_fulfillment_alert_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_DELAY_MS = "missing_fulfillment_alert_delay_ms";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_ENABLED = "missing_fulfillment_auto_fail_enabled";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS = "missing_fulfillment_auto_fail_delay_ms";

    private final CustomRewardType type;
    private final boolean autoSendRewardEmailEnabled;
    private final Boolean autoFulfillmentEnabled;
    private final Boolean missingFulfillmentAlertEnabled;
    private final Long missingFulfillmentAlertDelayMs;
    private final Boolean missingFulfillmentAutoFailEnabled;
    private final Long missingFulfillmentAutoFailDelayMs;

    public CustomRewardSupplierResponse(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
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
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType,
        @JsonProperty(TYPE) CustomRewardType type,
        @JsonProperty(REWARD_EMAIL_AUTO_SEND_ENABLED) boolean autoSendRewardEmailEnabled,
        @JsonProperty(AUTO_FULFILLMENT_ENABLED) Boolean autoFulfillmentEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_ENABLED) Boolean missingFulfillmentAlertEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_DELAY_MS) Long missingFulfillmentAlertDelayMs,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_ENABLED) Boolean missingFulfillmentAutoFailEnabled,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS) Long missingFulfillmentAutoFailDelayMs,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.CUSTOM_REWARD, id, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            name, faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack, limitPerDay,
            limitPerHour, faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data,
            enabled, stateTransitions);
        this.type = type;
        this.autoSendRewardEmailEnabled = autoSendRewardEmailEnabled;
        this.autoFulfillmentEnabled = autoFulfillmentEnabled;
        this.missingFulfillmentAlertEnabled = missingFulfillmentAlertEnabled;
        this.missingFulfillmentAlertDelayMs = missingFulfillmentAlertDelayMs;
        this.missingFulfillmentAutoFailEnabled = missingFulfillmentAutoFailEnabled;
        this.missingFulfillmentAutoFailDelayMs = missingFulfillmentAutoFailDelayMs;
    }

    @JsonProperty(TYPE)
    public CustomRewardType getType() {
        return type;
    }

    @JsonProperty(REWARD_EMAIL_AUTO_SEND_ENABLED)
    public boolean isAutoSendRewardEmailEnabled() {
        return autoSendRewardEmailEnabled;
    }

    @JsonProperty(AUTO_FULFILLMENT_ENABLED)
    public Boolean isAutoFulfillmentEnabled() {
        return autoFulfillmentEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_ALERT_ENABLED)
    public Boolean isMissingFulfillmentAlertEnabled() {
        return missingFulfillmentAlertEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_ALERT_DELAY_MS)
    public Long getMissingFulfillmentAlertDelayMs() {
        return missingFulfillmentAlertDelayMs;
    }

    @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_ENABLED)
    public Boolean isMissingFulfillmentAutoFailEnabled() {
        return missingFulfillmentAutoFailEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS)
    public Long getMissingFulfillmentAutoFailDelayMs() {
        return missingFulfillmentAutoFailDelayMs;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
