package com.extole.client.rest.reward.supplier.v2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.CustomRewardType;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CustomRewardSupplierUpdateV2Request extends ComponentElementRequest {

    private static final String NAME = "name";
    private static final String FACE_VALUE_ALGORITHM_TYPE = "face_value_algorithm_type";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String CASH_BACK_PERCENTAGE = "cash_back_percentage";
    private static final String CASH_BACK_MIN = "cash_back_min";
    private static final String CASH_BACK_MAX = "cash_back_max";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String PARTNER_REWARD_KEY_TYPE = "partner_reward_key_type";
    private static final String DISPLAY_TYPE = "display_type";
    private static final String TYPE = "type";
    private static final String REWARD_EMAIL_AUTO_SEND_ENABLED = "reward_email_auto_send_enabled";
    private static final String AUTO_FULFILLMENT_ENABLED = "auto_fulfillment_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_ENABLED = "missing_fulfillment_alert_enabled";
    private static final String MISSING_FULFILLMENT_ALERT_DELAY_MS = "missing_fulfillment_alert_delay_ms";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_ENABLED = "missing_fulfillment_auto_fail_enabled";
    private static final String MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS = "missing_fulfillment_auto_fail_delay_ms";
    private static final String LIMIT_PER_DAY = "limit_per_day";
    private static final String LIMIT_PER_HOUR = "limit_per_hour";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String ENABLED = "enabled";
    private static final String STATE_TRANSITIONS = "state_transitions";

    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name;
    private final Omissible<
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType>> faceValueType;
    private final Omissible<String> partnerRewardSupplierId;
    private final Omissible<PartnerRewardKeyType> partnerRewardKeyType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType;
    private final Omissible<CustomRewardType> type;
    private final Omissible<Boolean> autoSendRewardEmailEnabled;
    private final Omissible<Boolean> autoFulfillmentEnabled;
    private final Omissible<Boolean> missingFulfillmentAlertEnabled;
    private final Omissible<Long> missingFulfillmentAlertDelayMs;
    private final Omissible<Boolean> missingFulfillmentAutoFailEnabled;
    private final Omissible<Long> missingFulfillmentAutoFailDelayMs;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour;
    private final Omissible<Set<String>> tags;
    private final Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled;
    private final Omissible<Map<RewardState, List<RewardState>>> stateTransitions;

    public CustomRewardSupplierUpdateV2Request(
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
        @JsonProperty(FACE_VALUE_TYPE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType>> faceValueType,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Omissible<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) Omissible<PartnerRewardKeyType> partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType,
        @JsonProperty(TYPE) Omissible<CustomRewardType> type,
        @JsonProperty(REWARD_EMAIL_AUTO_SEND_ENABLED) Omissible<Boolean> autoSendRewardEmailEnabled,
        @JsonProperty(AUTO_FULFILLMENT_ENABLED) Omissible<Boolean> autoFulfillmentEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_ENABLED) Omissible<Boolean> missingFulfillmentAlertEnabled,
        @JsonProperty(MISSING_FULFILLMENT_ALERT_DELAY_MS) Omissible<Long> missingFulfillmentAlertDelayMs,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_ENABLED) Omissible<Boolean> missingFulfillmentAutoFailEnabled,
        @JsonProperty(MISSING_FULFILLMENT_AUTO_FAIL_DELAY_MS) Omissible<Long> missingFulfillmentAutoFailDelayMs,
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
        super(componentReferences, componentIds);
        this.name = name;
        this.faceValueAlgorithmType = faceValueAlgorithmType;
        this.faceValue = faceValue;
        this.cashBackPercentage = cashBackPercentage;
        this.minCashBack = minCashBack;
        this.maxCashBack = maxCashBack;
        this.faceValueType = faceValueType;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.displayType = displayType;
        this.type = type;
        this.autoSendRewardEmailEnabled = autoSendRewardEmailEnabled;
        this.autoFulfillmentEnabled = autoFulfillmentEnabled;
        this.missingFulfillmentAlertEnabled = missingFulfillmentAlertEnabled;
        this.missingFulfillmentAlertDelayMs = missingFulfillmentAlertDelayMs;
        this.missingFulfillmentAutoFailEnabled = missingFulfillmentAutoFailEnabled;
        this.missingFulfillmentAutoFailDelayMs = missingFulfillmentAutoFailDelayMs;
        this.limitPerDay = limitPerDay;
        this.limitPerHour = limitPerHour;
        this.tags = tags;
        this.data = data;
        this.enabled = enabled;
        this.stateTransitions = stateTransitions;
    }

    @JsonProperty(NAME)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>>
        getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> getFaceValue() {
        return faceValue;
    }

    @JsonProperty(CASH_BACK_PERCENTAGE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> getCashBackPercentage() {
        return cashBackPercentage;
    }

    @JsonProperty(CASH_BACK_MIN)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> getMinCashBack() {
        return minCashBack;
    }

    @JsonProperty(CASH_BACK_MAX)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> getMaxCashBack() {
        return maxCashBack;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType>> getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public Omissible<String> getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(PARTNER_REWARD_KEY_TYPE)
    public Omissible<PartnerRewardKeyType> getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @JsonProperty(DISPLAY_TYPE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getDisplayType() {
        return displayType;
    }

    @JsonProperty(TYPE)
    public Omissible<CustomRewardType> getType() {
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

    @JsonProperty(LIMIT_PER_DAY)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> getLimitPerDay() {
        return limitPerDay;
    }

    @JsonProperty(LIMIT_PER_HOUR)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> getLimitPerHour() {
        return limitPerHour;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> getData() {
        return data;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(STATE_TRANSITIONS)
    public Omissible<Map<RewardState, List<RewardState>>> getStateTransitions() {
        return stateTransitions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static CustomRewardSupplierUpdateRequestBuilder builder() {
        return new CustomRewardSupplierUpdateRequestBuilder();
    }

    public static final class CustomRewardSupplierUpdateRequestBuilder
        extends ComponentElementRequest.Builder<CustomRewardSupplierUpdateRequestBuilder> {
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType>> faceValueType =
            Omissible.omitted();
        private Omissible<String> partnerRewardSupplierId = Omissible.omitted();
        private Omissible<PartnerRewardKeyType> partnerRewardKeyType = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType =
            Omissible.omitted();
        private Omissible<CustomRewardType> type = Omissible.omitted();
        private Omissible<Boolean> autoSendRewardEmailEnabled = Omissible.omitted();
        private Omissible<Boolean> autoFulfillmentEnabled = Omissible.omitted();
        private Omissible<Boolean> missingFulfillmentAlertEnabled = Omissible.omitted();
        private Omissible<Long> missingFulfillmentAlertDelayMs = Omissible.omitted();
        private Omissible<Boolean> missingFulfillmentAutoFailEnabled = Omissible.omitted();
        private Omissible<Long> missingFulfillmentAutoFailDelayMs = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<Map<RewardState, List<RewardState>>> stateTransitions = Omissible.omitted();

        private CustomRewardSupplierUpdateRequestBuilder() {
        }

        public CustomRewardSupplierUpdateRequestBuilder withName(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withFaceValueAlgorithmType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType) {
            this.faceValueAlgorithmType = Omissible.of(faceValueAlgorithmType);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withFaceValue(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue) {
            this.faceValue = Omissible.of(faceValue);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withCashBackPercentage(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage) {
            this.cashBackPercentage = Omissible.of(cashBackPercentage);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withMinCashBack(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack) {
            this.minCashBack = Omissible.of(minCashBack);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withMaxCashBack(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack) {
            this.maxCashBack = Omissible.of(maxCashBack);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withFaceValueType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType) {
            this.faceValueType = Omissible.of(faceValueType);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withPartnerRewardSupplierId(String partnerRewardSupplierId) {
            this.partnerRewardSupplierId = Omissible.of(partnerRewardSupplierId);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withPartnerRewardKeyType(
            PartnerRewardKeyType partnerRewardKeyType) {
            this.partnerRewardKeyType = Omissible.of(partnerRewardKeyType);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withDisplayType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType) {
            this.displayType = Omissible.of(displayType);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withType(CustomRewardType type) {
            this.type = Omissible.of(type);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withAutoSendRewardEmailEnabled(Boolean autoSendRewardEmailEnabled) {
            this.autoSendRewardEmailEnabled = Omissible.of(autoSendRewardEmailEnabled);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withAutoFulfillmentEnabled(Boolean autoFulfillmentEnabled) {
            this.autoFulfillmentEnabled = Omissible.of(autoFulfillmentEnabled);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withMissingFulfillmentAlertEnabled(Boolean missingFulfillmentAlertEnabled) {
            this.missingFulfillmentAlertEnabled = Omissible.of(missingFulfillmentAlertEnabled);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withMissingFulfillmentAlertDelayMs(Long missingFulfillmentAlertDelayMs) {
            this.missingFulfillmentAlertDelayMs = Omissible.of(missingFulfillmentAlertDelayMs);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withMissingFulfillmentAutoFailEnabled(Boolean missingFulfillmentAutoFailEnabled) {
            this.missingFulfillmentAutoFailEnabled = Omissible.of(missingFulfillmentAutoFailEnabled);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withMissingFulfillmentAutoFailDelayMs(Long missingFulfillmentAutoFailDelayMs) {
            this.missingFulfillmentAutoFailDelayMs = Omissible.of(missingFulfillmentAutoFailDelayMs);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withLimitPerDay(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay) {
            this.limitPerDay = Omissible.of(limitPerDay);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withLimitPerHour(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour) {
            this.limitPerHour = Omissible.of(limitPerHour);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withData(
            Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withEnabled(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withStateTransitions(
            Map<RewardState, List<RewardState>> stateTransitions) {
            this.stateTransitions = Omissible.of(stateTransitions);
            return this;
        }

        public CustomRewardSupplierUpdateV2Request build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CustomRewardSupplierUpdateV2Request(name,
                faceValueAlgorithmType,
                faceValue,
                cashBackPercentage,
                minCashBack, maxCashBack,
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
                limitPerDay,
                limitPerHour,
                componentIds,
                componentReferences,
                tags,
                data,
                enabled,
                stateTransitions);
        }

        public CustomRewardSupplierUpdateRequestBuilder withName(String name) {
            this.name = Omissible.of(Provided.of(name));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withFaceValue(BigDecimal faceValue) {
            this.faceValue = Omissible.of(Provided.of(faceValue));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withFaceValueType(FaceValueType faceValueType) {
            this.faceValueType = Omissible.of(Provided.of(faceValueType));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withCashBackPercentage(BigDecimal cashBackPercentage) {
            this.cashBackPercentage = Omissible.of(Provided.of(cashBackPercentage));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withMinCashBack(BigDecimal minCashBack) {
            this.minCashBack = Omissible.of(Provided.of(minCashBack));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder withMaxCashBack(BigDecimal maxCashBack) {
            this.maxCashBack = Omissible.of(Provided.of(maxCashBack));
            return this;
        }

        public CustomRewardSupplierUpdateRequestBuilder
            withFaceValueAlgorithmType(FaceValueAlgorithmType faceValueAlgorithmType) {
            this.faceValueAlgorithmType = Omissible.of(Provided.of(faceValueAlgorithmType));
            return this;
        }
    }
}
