package com.extole.client.rest.reward.supplier;

import static com.extole.client.rest.reward.supplier.RewardSupplierCreateRequest.REWARD_SUPPLIER_TYPE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.reward.supplier.built.RewardSupplierBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.custom.CustomRewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.manual.ManualCouponRewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.paypal.PayPalPayoutsRewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.salesforce.SalesforceCouponRewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.tango.TangoRewardSupplierCreateRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = REWARD_SUPPLIER_TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CustomRewardSupplierCreateRequest.class,
        name = CustomRewardSupplierCreateRequest.CUSTOM_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = ManualCouponRewardSupplierCreateRequest.class,
        name = ManualCouponRewardSupplierCreateRequest.MANUAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = PayPalPayoutsRewardSupplierCreateRequest.class,
        name = PayPalPayoutsRewardSupplierCreateRequest.PAYPAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = SalesforceCouponRewardSupplierCreateRequest.class,
        name = SalesforceCouponRewardSupplierCreateRequest.SALESFORCE_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = TangoRewardSupplierCreateRequest.class,
        name = TangoRewardSupplierCreateRequest.TANGO_REWARD_SUPPLIER_TYPE)
})
@Schema(discriminatorProperty = REWARD_SUPPLIER_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CustomRewardSupplierCreateRequest.CUSTOM_REWARD_SUPPLIER_TYPE,
        schema = CustomRewardSupplierCreateRequest.class),
    @DiscriminatorMapping(value = ManualCouponRewardSupplierCreateRequest.MANUAL_REWARD_SUPPLIER_TYPE,
        schema = ManualCouponRewardSupplierCreateRequest.class),
    @DiscriminatorMapping(value = PayPalPayoutsRewardSupplierCreateRequest.PAYPAL_REWARD_SUPPLIER_TYPE,
        schema = PayPalPayoutsRewardSupplierCreateRequest.class),
    @DiscriminatorMapping(value = SalesforceCouponRewardSupplierCreateRequest.SALESFORCE_REWARD_SUPPLIER_TYPE,
        schema = SalesforceCouponRewardSupplierCreateRequest.class),
    @DiscriminatorMapping(value = TangoRewardSupplierCreateRequest.TANGO_REWARD_SUPPLIER_TYPE,
        schema = TangoRewardSupplierCreateRequest.class)
})
public abstract class RewardSupplierCreateRequest extends ComponentElementRequest {
    protected static final String REWARD_SUPPLIER_TYPE = "reward_supplier_type";
    protected static final String NAME = "name";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String FACE_VALUE_ALGORITHM_TYPE = "face_value_algorithm_type";
    protected static final String FACE_VALUE = "face_value";
    protected static final String FACE_VALUE_TYPE = "face_value_type";
    protected static final String CASH_BACK_PERCENTAGE = "cash_back_percentage";
    protected static final String CASH_BACK_MIN = "cash_back_min";
    protected static final String CASH_BACK_MAX = "cash_back_max";
    protected static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    protected static final String PARTNER_REWARD_KEY_TYPE = "partner_reward_key_type";
    protected static final String DISPLAY_TYPE = "display_type";
    protected static final String DESCRIPTION = "description";
    protected static final String LIMIT_PER_DAY = "limit_per_day";
    protected static final String LIMIT_PER_HOUR = "limit_per_hour";
    protected static final String TAGS = "tags";
    protected static final String DATA = "data";
    protected static final String ENABLED = "enabled";

    private final RewardSupplierType rewardSupplierType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> displayName;
    private final Omissible<
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
    private final Omissible<String> partnerRewardSupplierId;
    private final Omissible<PartnerRewardKeyType> partnerRewardKeyType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour;
    private final Omissible<Set<String>> tags;
    private final Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data;
    private final Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled;

    protected RewardSupplierCreateRequest(
        RewardSupplierType rewardSupplierType,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> displayName,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType,
        Omissible<String> partnerRewardSupplierId,
        Omissible<PartnerRewardKeyType> partnerRewardKeyType,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences,
        Omissible<Set<String>> tags,
        Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data,
        Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled) {
        super(componentReferences, componentIds);
        this.rewardSupplierType = rewardSupplierType;
        this.name = name;
        this.displayName = displayName;
        this.faceValueAlgorithmType = faceValueAlgorithmType;
        this.faceValue = faceValue;
        this.cashBackPercentage = cashBackPercentage;
        this.minCashBack = minCashBack;
        this.maxCashBack = maxCashBack;
        this.faceValueType = faceValueType;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.displayType = displayType;
        this.description = description;
        this.limitPerDay = limitPerDay;
        this.limitPerHour = limitPerHour;
        this.tags = tags;
        this.data = data;
        this.enabled = enabled;
    }

    @JsonProperty(REWARD_SUPPLIER_TYPE)
    public RewardSupplierType getRewardSupplierType() {
        return rewardSupplierType;
    }

    @JsonProperty(NAME)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> getDisplayName() {
        return displayName;
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
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> getFaceValueType() {
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

    public abstract static class Builder<REQUEST extends RewardSupplierCreateRequest, BUILDER extends RewardSupplierCreateRequest.Builder<
        REQUEST, BUILDER>>
        extends ComponentElementRequest.Builder<BUILDER> {
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> displayName =
            Omissible.omitted();
        protected Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType>> faceValueAlgorithmType =
                Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> faceValue =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> cashBackPercentage =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> minCashBack =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal>> maxCashBack =
            Omissible.omitted();
        protected BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
        protected Omissible<String> partnerRewardSupplierId = Omissible.omitted();
        protected Omissible<PartnerRewardKeyType> partnerRewardKeyType = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> displayType =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerDay =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>>> limitPerHour =
            Omissible.omitted();
        protected Omissible<Set<String>> tags = Omissible.omitted();
        protected Omissible<Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>>> data =
            Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean>> enabled =
            Omissible.omitted();
        protected Omissible<Map<RewardState, List<RewardState>>> stateTransitions = Omissible.omitted();

        protected Builder() {
        }

        public BUILDER withName(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return (BUILDER) this;
        }

        public BUILDER withDisplayName(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName) {
            this.displayName = Omissible.of(displayName);
            return (BUILDER) this;
        }

        public BUILDER withFaceValueAlgorithmType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType) {
            this.faceValueAlgorithmType = Omissible.of(faceValueAlgorithmType);
            return (BUILDER) this;
        }

        public BUILDER withFaceValue(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue) {
            this.faceValue = Omissible.of(faceValue);
            return (BUILDER) this;
        }

        public BUILDER withCashBackPercentage(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage) {
            this.cashBackPercentage = Omissible.of(cashBackPercentage);
            return (BUILDER) this;
        }

        public BUILDER withMinCashBack(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack) {
            this.minCashBack = Omissible.of(minCashBack);
            return (BUILDER) this;
        }

        public BUILDER withMaxCashBack(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack) {
            this.maxCashBack = Omissible.of(maxCashBack);
            return (BUILDER) this;
        }

        public BUILDER withFaceValueType(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType) {
            this.faceValueType = faceValueType;
            return (BUILDER) this;
        }

        public BUILDER withPartnerRewardSupplierId(String partnerRewardSupplierId) {
            this.partnerRewardSupplierId = Omissible.of(partnerRewardSupplierId);
            return (BUILDER) this;
        }

        public BUILDER withPartnerRewardKeyType(PartnerRewardKeyType partnerRewardKeyType) {
            this.partnerRewardKeyType = Omissible.of(partnerRewardKeyType);
            return (BUILDER) this;
        }

        public BUILDER withDisplayType(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType) {
            this.displayType = Omissible.of(displayType);
            return (BUILDER) this;
        }

        public BUILDER withDescription(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return (BUILDER) this;
        }

        public BUILDER withLimitPerDay(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay) {
            this.limitPerDay = Omissible.of(limitPerDay);
            return (BUILDER) this;
        }

        public BUILDER withLimitPerHour(
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour) {
            this.limitPerHour = Omissible.of(limitPerHour);
            return (BUILDER) this;
        }

        public BUILDER withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return (BUILDER) this;
        }

        public BUILDER withData(Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data) {
            this.data = Omissible.of(data);
            return (BUILDER) this;
        }

        public BUILDER withEnabled(BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return (BUILDER) this;
        }

        public abstract REQUEST build();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
