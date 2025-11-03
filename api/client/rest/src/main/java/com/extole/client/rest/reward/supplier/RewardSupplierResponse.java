package com.extole.client.rest.reward.supplier;

import static com.extole.client.rest.reward.supplier.RewardSupplierResponse.REWARD_SUPPLIER_TYPE;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
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
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.custom.CustomRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.manual.ManualCouponRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.paypal.PayPalPayoutsRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.salesforce.SalesforceCouponRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.tango.TangoRewardSupplierResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = REWARD_SUPPLIER_TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CustomRewardSupplierResponse.class,
        name = CustomRewardSupplierResponse.CUSTOM_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = ManualCouponRewardSupplierResponse.class,
        name = ManualCouponRewardSupplierResponse.MANUAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = PayPalPayoutsRewardSupplierResponse.class,
        name = PayPalPayoutsRewardSupplierResponse.PAYPAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = SalesforceCouponRewardSupplierResponse.class,
        name = SalesforceCouponRewardSupplierResponse.SALESFORCE_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = TangoRewardSupplierResponse.class,
        name = TangoRewardSupplierResponse.TANGO_REWARD_SUPPLIER_TYPE)
})
@Schema(discriminatorProperty = REWARD_SUPPLIER_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CustomRewardSupplierResponse.CUSTOM_REWARD_SUPPLIER_TYPE,
        schema = CustomRewardSupplierResponse.class),
    @DiscriminatorMapping(value = ManualCouponRewardSupplierResponse.MANUAL_REWARD_SUPPLIER_TYPE,
        schema = ManualCouponRewardSupplierResponse.class),
    @DiscriminatorMapping(value = PayPalPayoutsRewardSupplierResponse.PAYPAL_REWARD_SUPPLIER_TYPE,
        schema = PayPalPayoutsRewardSupplierResponse.class),
    @DiscriminatorMapping(value = SalesforceCouponRewardSupplierResponse.SALESFORCE_REWARD_SUPPLIER_TYPE,
        schema = SalesforceCouponRewardSupplierResponse.class),
    @DiscriminatorMapping(value = TangoRewardSupplierResponse.TANGO_REWARD_SUPPLIER_TYPE,
        schema = TangoRewardSupplierResponse.class)
})
public abstract class RewardSupplierResponse extends ComponentElementResponse {
    protected static final String REWARD_SUPPLIER_ID = "id";
    protected static final String REWARD_SUPPLIER_TYPE = "reward_supplier_type";
    protected static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    protected static final String PARTNER_REWARD_KEY_TYPE = "partner_reward_key_type";
    protected static final String DISPLAY_TYPE = "display_type";
    protected static final String NAME = "name";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String FACE_VALUE_ALGORITHM_TYPE = "face_value_algorithm_type";
    protected static final String FACE_VALUE = "face_value";
    protected static final String FACE_VALUE_TYPE = "face_value_type";
    protected static final String CASH_BACK_PERCENTAGE = "cash_back_percentage";
    protected static final String CASH_BACK_MIN = "cash_back_min";
    protected static final String CASH_BACK_MAX = "cash_back_max";
    protected static final String LIMIT_PER_DAY = "limit_per_day";
    protected static final String LIMIT_PER_HOUR = "limit_per_hour";
    protected static final String CREATED_DATE = "created_date";
    protected static final String UPDATED_DATE = "updated_date";
    protected static final String TAGS = "tags";
    protected static final String DATA = "data";
    protected static final String ENABLED = "enabled";
    protected static final String STATE_TRANSITIONS = "state_transitions";

    private final String id;
    private final RewardSupplierType rewardSupplierType;
    private final Optional<String> partnerRewardSupplierId;
    private final PartnerRewardKeyType partnerRewardKeyType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Set<String> tags;
    private final Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data;
    private final BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled;
    private final Map<RewardState, List<RewardState>> stateTransitions;

    public RewardSupplierResponse(
        RewardSupplierType rewardSupplierType,
        String id,
        Optional<String> partnerRewardSupplierId,
        PartnerRewardKeyType partnerRewardKeyType,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> displayType,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> name,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> displayName,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> faceValueAlgorithmType,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> faceValue,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> cashBackPercentage,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> minCashBack,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> maxCashBack,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerDay,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> limitPerHour,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> faceValueType,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate,
        List<Id<ComponentResponse>> componentIds,
        List<ComponentReferenceResponse> componentReferences,
        Set<String> tags,
        Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> data,
        BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> enabled,
        Map<RewardState, List<RewardState>> stateTransitions) {
        super(componentReferences, componentIds);
        this.rewardSupplierType = rewardSupplierType;
        this.id = id;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.displayType = displayType;
        this.name = name;
        this.displayName = displayName;
        this.faceValueAlgorithmType = faceValueAlgorithmType;
        this.faceValue = faceValue;
        this.cashBackPercentage = cashBackPercentage;
        this.minCashBack = minCashBack;
        this.maxCashBack = maxCashBack;
        this.limitPerDay = limitPerDay;
        this.limitPerHour = limitPerHour;
        this.faceValueType = faceValueType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.tags = tags;
        this.data = data;
        this.enabled = enabled;
        this.stateTransitions = stateTransitions;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return id;
    }

    @JsonProperty(REWARD_SUPPLIER_TYPE)
    public RewardSupplierType getRewardSupplierType() {
        return rewardSupplierType;
    }

    @JsonProperty(PARTNER_REWARD_SUPPLIER_ID)
    public Optional<String> getPartnerRewardSupplierId() {
        return partnerRewardSupplierId;
    }

    @JsonProperty(PARTNER_REWARD_KEY_TYPE)
    public PartnerRewardKeyType getPartnerRewardKeyType() {
        return partnerRewardKeyType;
    }

    @JsonProperty(DISPLAY_TYPE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getDisplayType() {
        return displayType;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(DISPLAY_NAME)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueAlgorithmType> getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getFaceValue() {
        return faceValue;
    }

    @JsonProperty(CASH_BACK_PERCENTAGE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getCashBackPercentage() {
        return cashBackPercentage;
    }

    @JsonProperty(CASH_BACK_MIN)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMinCashBack() {
        return minCashBack;
    }

    @JsonProperty(CASH_BACK_MAX)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, BigDecimal> getMaxCashBack() {
        return maxCashBack;
    }

    @JsonProperty(LIMIT_PER_DAY)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> getLimitPerDay() {
        return limitPerDay;
    }

    @JsonProperty(LIMIT_PER_HOUR)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<Integer>> getLimitPerHour() {
        return limitPerHour;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, FaceValueType> getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<String, BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> getData() {
        return data;
    }

    @JsonProperty(ENABLED)
    public BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(STATE_TRANSITIONS)
    public Map<RewardState, List<RewardState>> getStateTransitions() {
        return stateTransitions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
