package com.extole.client.rest.reward.supplier.custom;

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
import com.extole.client.rest.reward.supplier.CustomRewardType;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CustomRewardSupplierCreateRequest extends RewardSupplierCreateRequest {

    public static final String CUSTOM_REWARD_SUPPLIER_TYPE = "CUSTOM_REWARD";

    private static final String TYPE = "type";
    private static final String REWARD_EMAIL_AUTO_SEND_ENABLED = "reward_email_auto_send_enabled";
    private static final String AUTO_FULFILLMENT_ENABLED = "auto_fulfillment_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_ENABLED = "missing_fulfillment_alert_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_DELAY_MS = "missing_fulfillment_alert_delay_ms";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_ENABLED = "missing_fulfillment_auto_fail_enabled";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS = "missing_fulfillment_auto_fail_delay_ms";
    private static final String STATE_TRANSITIONS = "state_transitions";

    private final CustomRewardType type;
    private final Omissible<Boolean> autoSendRewardEmailEnabled;
    private final Omissible<Boolean> autoFulfillmentEnabled;
    private final Omissible<Boolean> missingFulfillmentAlertEnabled;
    private final Omissible<Long> missingFulfillmentAlertDelayMs;
    private final Omissible<Boolean> missingFulfillmentAutoFailEnabled;
    private final Omissible<Long> missingFulfillmentAutoFailDelayMs;
    private final Omissible<Map<RewardState, List<RewardState>>> stateTransitions;

    public CustomRewardSupplierCreateRequest(
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack,
        @JsonProperty(CASH_BACK_MAX) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack,
        @JsonProperty(FACE_VALUE_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            FaceValueType> faceValueType,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Omissible<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) Omissible<PartnerRewardKeyType> partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType,
        @JsonProperty(TYPE) CustomRewardType type,
        @JsonProperty(REWARD_EMAIL_AUTO_SEND_ENABLED) Omissible<Boolean> autoSendRewardEmailEnabled,
        @JsonProperty(AUTO_FULFILLMENT_ENABLED) Omissible<Boolean> autoFulfillmentEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_ENABLED) Omissible<Boolean> missingFulfillmentAlertEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_DELAY_MS) Omissible<Long> missingFulfillmentAlertDelayMs,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_ENABLED) Omissible<Boolean> missingFulfillmentAutoFailEnabled,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS) Omissible<Long> missingFulfillmentAutoFailDelayMs,
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
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled,
        @JsonProperty(STATE_TRANSITIONS) Omissible<Map<RewardState, List<RewardState>>> stateTransitions) {
        super(RewardSupplierType.CUSTOM_REWARD, name, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, faceValueType, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            description, limitPerDay, limitPerHour, componentIds, componentReferences, tags, data, enabled);
        this.type = type;
        this.autoSendRewardEmailEnabled = autoSendRewardEmailEnabled;
        this.autoFulfillmentEnabled = autoFulfillmentEnabled;
        this.missingFulfillmentAlertEnabled = missingFulfillmentAlertEnabled;
        this.missingFulfillmentAlertDelayMs = missingFulfillmentAlertDelayMs;
        this.missingFulfillmentAutoFailEnabled = missingFulfillmentAutoFailEnabled;
        this.missingFulfillmentAutoFailDelayMs = missingFulfillmentAutoFailDelayMs;
        this.stateTransitions = stateTransitions;
    }

    @JsonProperty(TYPE)
    public CustomRewardType getType() {
        return type;
    }

    @JsonProperty(REWARD_EMAIL_AUTO_SEND_ENABLED)
    public Omissible<Boolean> isAutoSendRewardEmailEnabled() {
        return autoSendRewardEmailEnabled;
    }

    @JsonProperty(AUTO_FULFILLMENT_ENABLED)
    public Omissible<Boolean> isAutoFulfillmentEnabled() {
        return autoFulfillmentEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_ALERT_ENABLED)
    public Omissible<Boolean> isMissingFulfillmentAlertEnabled() {
        return missingFulfillmentAlertEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_ALERT_DELAY_MS)
    public Omissible<Long> getMissingFulfillmentAlertDelayMs() {
        return missingFulfillmentAlertDelayMs;
    }

    @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_ENABLED)
    public Omissible<Boolean> isMissingFulfillmentAutoFailEnabled() {
        return missingFulfillmentAutoFailEnabled;
    }

    @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS)
    public Omissible<Long> getMissingFulfillmentAutoFailDelayMs() {
        return missingFulfillmentAutoFailDelayMs;
    }

    @JsonProperty(STATE_TRANSITIONS)
    public com.extole.common.rest.omissible.Omissible<java.util.Map<com.extole.client.rest.reward.supplier.RewardState,
        java.util.List<com.extole.client.rest.reward.supplier.RewardState>>>
        getStateTransitions() {
        return stateTransitions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends RewardSupplierCreateRequest.Builder<CustomRewardSupplierCreateRequest, Builder> {
        private CustomRewardType type;
        private Omissible<Boolean> autoSendRewardEmailEnabled = Omissible.omitted();
        private Omissible<Boolean> autoFulfillmentEnabled = Omissible.omitted();
        private Omissible<Boolean> missingFulfillmentAlertEnabled = Omissible.omitted();
        private Omissible<Long> missingFulfillmentAlertDelayMs = Omissible.omitted();
        private Omissible<Boolean> missingFulfillmentAutoFailEnabled = Omissible.omitted();
        private Omissible<Long> missingFulfillmentAutoFailDelayMs = Omissible.omitted();

        private Builder() {
        }

        public Builder withType(CustomRewardType type) {
            this.type = type;
            return this;
        }

        public Builder
            withAutoSendRewardEmailEnabled(Boolean autoSendRewardEmailEnabled) {
            this.autoSendRewardEmailEnabled = Omissible.of(autoSendRewardEmailEnabled);
            return this;
        }

        public Builder
            withAutoFulfillmentEnabled(Boolean autoFulfillmentEnabled) {
            this.autoFulfillmentEnabled = Omissible.of(autoFulfillmentEnabled);
            return this;
        }

        public Builder
            withMissingFulfillmentAlertEnabled(Boolean missingFulfillmentAlertEnabled) {
            this.missingFulfillmentAlertEnabled = Omissible.of(missingFulfillmentAlertEnabled);
            return this;
        }

        public Builder
            withMissingFulfillmentAlertDelayMs(Long missingFulfillmentAlertDelayMs) {
            this.missingFulfillmentAlertDelayMs = Omissible.of(missingFulfillmentAlertDelayMs);
            return this;
        }

        public Builder
            withMissingFulfillmentAutoFailEnabled(Boolean missingFulfillmentAutoFailEnabled) {
            this.missingFulfillmentAutoFailEnabled = Omissible.of(missingFulfillmentAutoFailEnabled);
            return this;
        }

        public Builder
            withMissingFulfillmentAutoFailDelayMs(Long missingFulfillmentAutoFailDelayMs) {
            this.missingFulfillmentAutoFailDelayMs = Omissible.of(missingFulfillmentAutoFailDelayMs);
            return this;
        }

        public Builder withStateTransitions(Map<RewardState, List<RewardState>> stateTransitions) {
            this.stateTransitions = Omissible.of(stateTransitions);
            return this;
        }

        public CustomRewardSupplierCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CustomRewardSupplierCreateRequest(
                name,
                faceValueAlgorithmType,
                faceValue,
                cashBackPercentage,
                minCashBack,
                maxCashBack,
                faceValueType,
                partnerRewardSupplierId,
                partnerRewardKeyType,
                displayType,
                type,
                autoSendRewardEmailEnabled,
                autoFulfillmentEnabled,
                missingFulfillmentAlertEnabled,
                missingFulfillmentAlertDelayMs,
                missingFulfillmentAutoFailEnabled,
                missingFulfillmentAutoFailDelayMs,
                description,
                limitPerDay,
                limitPerHour,
                componentIds,
                componentReferences,
                tags,
                data,
                enabled,
                stateTransitions);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
