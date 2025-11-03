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
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class PayPalPayoutsRewardSupplierCreationV2Request extends ComponentElementRequest {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "display_name";
    private static final String MERCHANT_TOKEN = "merchant_token";
    private static final String FACE_VALUE_ALGORITHM_TYPE = "face_value_algorithm_type";
    private static final String FACE_VALUE = "face_value";
    private static final String FACE_VALUE_TYPE = "face_value_type";
    private static final String CASH_BACK_PERCENTAGE = "cash_back_percentage";
    private static final String CASH_BACK_MIN = "cash_back_min";
    private static final String CASH_BACK_MAX = "cash_back_max";
    private static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    private static final String DISPLAY_TYPE = "display_type";
    private static final String DESCRIPTION = "description";
    private static final String LIMIT_PER_DAY = "limit_per_day";
    private static final String LIMIT_PER_HOUR = "limit_per_hour";
    private static final String TAGS = "tags";
    private static final String DATA = "data";
    private static final String ENABLED = "enabled";

    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName;
    private final String merchantToken;
    private final Omissible<
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
    private final Omissible<String> partnerRewardSupplierId;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour;
    private final Omissible<Set<String>> tags;
    private final Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled;

    public PayPalPayoutsRewardSupplierCreationV2Request(
        @JsonProperty(NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name,
        @JsonProperty(DISPLAY_NAME) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName,
        @JsonProperty(MERCHANT_TOKEN) String merchantToken,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack,
        @JsonProperty(CASH_BACK_MAX) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack,
        @JsonProperty(FACE_VALUE_TYPE) BuildtimeEvaluatable<RewardSupplierBuildtimeContext,
            FaceValueType> faceValueType,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Omissible<String> partnerRewardSupplierId,
        @JsonProperty(DISPLAY_TYPE) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType,
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
        super(componentReferences, componentIds);
        this.name = name;
        this.displayName = displayName;
        this.merchantToken = merchantToken;
        this.faceValueAlgorithmType = faceValueAlgorithmType;
        this.faceValue = faceValue;
        this.cashBackPercentage = cashBackPercentage;
        this.minCashBack = minCashBack;
        this.maxCashBack = maxCashBack;
        this.faceValueType = faceValueType;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.displayType = displayType;
        this.description = description;
        this.limitPerDay = limitPerDay;
        this.limitPerHour = limitPerHour;
        this.tags = tags;
        this.data = data;
        this.enabled = enabled;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(MERCHANT_TOKEN)
    public String getMerchantToken() {
        return merchantToken;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>>
        getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
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
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public Omissible<String> getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(DISPLAY_TYPE)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getDisplayType() {
        return displayType;
    }

    @JsonProperty(DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> getDescription() {
        return description;
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

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name;
        private BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName;
        private String merchantToken;
        private Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType =
                Omissible.omitted();
        private BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue;
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack =
            Omissible.omitted();
        private BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
        private Omissible<String> partnerRewardSupplierId = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder
            withDisplayName(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withMerchantToken(String merchantToken) {
            this.merchantToken = merchantToken;
            return this;
        }

        public Builder withFaceValueAlgorithmType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType) {
            this.faceValueAlgorithmType = Omissible.of(faceValueAlgorithmType);
            return this;
        }

        public Builder withFaceValue(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue) {
            this.faceValue = faceValue;
            return this;
        }

        public Builder withCashBackPercentage(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage) {
            this.cashBackPercentage = Omissible.of(cashBackPercentage);
            return this;
        }

        public Builder withDisplayType(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType) {
            this.displayType = Omissible.of(displayType);
            return this;
        }

        public Builder withMinCashBack(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack) {
            this.minCashBack = Omissible.of(minCashBack);
            return this;
        }

        public Builder withMaxCashBack(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack) {
            this.maxCashBack = Omissible.of(maxCashBack);
            return this;
        }

        public Builder
            withFaceValueType(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType) {
            this.faceValueType = faceValueType;
            return this;
        }

        public Builder withPartnerRewardSupplierId(String partnerRewardSupplierId) {
            this.partnerRewardSupplierId = Omissible.of(partnerRewardSupplierId);
            return this;
        }

        public Builder
            withDescription(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder
            withLimitPerDay(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay) {
            this.limitPerDay = Omissible.of(limitPerDay);
            return this;
        }

        public Builder
            withLimitPerHour(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour) {
            this.limitPerHour = Omissible.of(limitPerHour);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withData(Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public PayPalPayoutsRewardSupplierCreationV2Request build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new PayPalPayoutsRewardSupplierCreationV2Request(name,
                displayName,
                merchantToken,
                faceValueAlgorithmType,
                faceValue,
                cashBackPercentage,
                minCashBack,
                maxCashBack,
                faceValueType,
                partnerRewardSupplierId,
                displayType,
                description,
                limitPerDay,
                limitPerHour,
                componentIds,
                componentReferences,
                tags,
                data,
                enabled);
        }

        public Builder withName(String name) {
            this.name = Provided.of(name);
            return this;
        }

        public Builder withFaceValueType(FaceValueType faceValueType) {
            this.faceValueType = Provided.of(faceValueType);
            return this;
        }

        public Builder withFaceValue(BigDecimal faceValue) {
            this.faceValue = Provided.of(faceValue);
            return this;
        }

        public Builder withFaceValueAlgorithmType(FaceValueAlgorithmType faceValueAlgorithmType) {
            this.faceValueAlgorithmType = Omissible.of(Provided.of(faceValueAlgorithmType));
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(Provided.optionalOf(description));
            return this;
        }

        public Builder withCashBackPercentage(BigDecimal cashBackPercentage) {
            this.cashBackPercentage = Omissible.of(Provided.of(cashBackPercentage));
            return this;
        }

        public Builder withMinCashBack(BigDecimal minCashBack) {
            this.minCashBack = Omissible.of(Provided.of(minCashBack));
            return this;
        }

        public Builder withMaxCashBack(BigDecimal maxCashBack) {
            this.maxCashBack = Omissible.of(Provided.of(maxCashBack));
            return this;
        }
    }
}
