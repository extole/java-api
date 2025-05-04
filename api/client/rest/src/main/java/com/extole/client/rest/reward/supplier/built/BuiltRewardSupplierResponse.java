package com.extole.client.rest.reward.supplier.built;

import static com.extole.client.rest.reward.supplier.built.BuiltRewardSupplierResponse.REWARD_SUPPLIER_TYPE;

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

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardState;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.client.rest.reward.supplier.custom.built.BuiltCustomRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.manual.built.BuiltManualCouponRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.paypal.built.BuiltPayPalPayoutsRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.salesforce.built.BuiltSalesforceCouponRewardSupplierResponse;
import com.extole.client.rest.reward.supplier.tango.built.BuiltTangoRewardSupplierResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = REWARD_SUPPLIER_TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BuiltCustomRewardSupplierResponse.class,
        name = BuiltCustomRewardSupplierResponse.CUSTOM_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = BuiltManualCouponRewardSupplierResponse.class,
        name = BuiltManualCouponRewardSupplierResponse.MANUAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = BuiltPayPalPayoutsRewardSupplierResponse.class,
        name = BuiltPayPalPayoutsRewardSupplierResponse.PAYPAL_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = BuiltSalesforceCouponRewardSupplierResponse.class,
        name = BuiltSalesforceCouponRewardSupplierResponse.SALESFORCE_REWARD_SUPPLIER_TYPE),
    @JsonSubTypes.Type(value = BuiltTangoRewardSupplierResponse.class,
        name = BuiltTangoRewardSupplierResponse.TANGO_REWARD_SUPPLIER_TYPE)
})
@Schema(discriminatorProperty = REWARD_SUPPLIER_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = BuiltCustomRewardSupplierResponse.CUSTOM_REWARD_SUPPLIER_TYPE,
        schema = BuiltCustomRewardSupplierResponse.class),
    @DiscriminatorMapping(value = BuiltManualCouponRewardSupplierResponse.MANUAL_REWARD_SUPPLIER_TYPE,
        schema = BuiltManualCouponRewardSupplierResponse.class),
    @DiscriminatorMapping(value = BuiltPayPalPayoutsRewardSupplierResponse.PAYPAL_REWARD_SUPPLIER_TYPE,
        schema = BuiltPayPalPayoutsRewardSupplierResponse.class),
    @DiscriminatorMapping(value = BuiltSalesforceCouponRewardSupplierResponse.SALESFORCE_REWARD_SUPPLIER_TYPE,
        schema = BuiltSalesforceCouponRewardSupplierResponse.class),
    @DiscriminatorMapping(value = BuiltTangoRewardSupplierResponse.TANGO_REWARD_SUPPLIER_TYPE,
        schema = BuiltTangoRewardSupplierResponse.class)
})
public abstract class BuiltRewardSupplierResponse extends ComponentElementResponse {
    protected static final String REWARD_SUPPLIER_ID = "id";
    protected static final String REWARD_SUPPLIER_TYPE = "reward_supplier_type";
    protected static final String PARTNER_REWARD_SUPPLIER_ID = "partner_reward_supplier_id";
    protected static final String PARTNER_REWARD_KEY_TYPE = "partner_reward_key_type";
    protected static final String DISPLAY_TYPE = "display_type";
    protected static final String NAME = "name";
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
    protected static final String COMPONENT_REFERENCES = "component_references";
    protected static final String TAGS = "tags";
    protected static final String DATA = "data";
    protected static final String ENABLED = "enabled";
    protected static final String STATE_TRANSITIONS = "state_transitions";

    private final String id;
    private final RewardSupplierType rewardSupplierType;
    private final Optional<String> partnerRewardSupplierId;
    private final PartnerRewardKeyType partnerRewardKeyType;
    private final String displayType;
    private final String name;
    private final FaceValueAlgorithmType faceValueAlgorithmType;
    private final BigDecimal faceValue;
    private final BigDecimal cashBackPercentage;
    private final BigDecimal minCashBack;
    private final BigDecimal maxCashBack;
    private final Optional<Integer> limitPerDay;
    private final Optional<Integer> limitPerHour;
    private final FaceValueType faceValueType;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final Set<String> tags;
    private final Map<String, String> data;
    private final Boolean enabled;
    private final Map<RewardState, List<RewardState>> stateTransitions;

    public BuiltRewardSupplierResponse(
        RewardSupplierType rewardSupplierType,
        String id,
        Optional<String> partnerRewardSupplierId,
        PartnerRewardKeyType partnerRewardKeyType,
        String displayType,
        String name,
        FaceValueAlgorithmType faceValueAlgorithmType,
        BigDecimal faceValue,
        BigDecimal cashBackPercentage,
        BigDecimal minCashBack,
        BigDecimal maxCashBack,
        Optional<Integer> limitPerDay,
        Optional<Integer> limitPerHour,
        FaceValueType faceValueType,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate,
        List<Id<ComponentResponse>> componentIds,
        List<ComponentReferenceResponse> componentReferences,
        Set<String> tags,
        Map<String, String> data,
        Boolean enabled,
        Map<RewardState, List<RewardState>> stateTransitions) {
        super(componentReferences, componentIds);
        this.rewardSupplierType = rewardSupplierType;
        this.id = id;
        this.partnerRewardSupplierId = partnerRewardSupplierId;
        this.partnerRewardKeyType = partnerRewardKeyType;
        this.displayType = displayType;
        this.name = name;
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
    public String getDisplayType() {
        return displayType;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(FACE_VALUE_ALGORITHM_TYPE)
    public FaceValueAlgorithmType getFaceValueAlgorithmType() {
        return faceValueAlgorithmType;
    }

    @JsonProperty(FACE_VALUE)
    public BigDecimal getFaceValue() {
        return faceValue;
    }

    @JsonProperty(CASH_BACK_PERCENTAGE)
    public BigDecimal getCashBackPercentage() {
        return cashBackPercentage;
    }

    @JsonProperty(CASH_BACK_MIN)
    public BigDecimal getMinCashBack() {
        return minCashBack;
    }

    @JsonProperty(CASH_BACK_MAX)
    public BigDecimal getMaxCashBack() {
        return maxCashBack;
    }

    @JsonProperty(LIMIT_PER_DAY)
    public Optional<Integer> getLimitPerDay() {
        return limitPerDay;
    }

    @JsonProperty(LIMIT_PER_HOUR)
    public Optional<Integer> getLimitPerHour() {
        return limitPerHour;
    }

    @JsonProperty(FACE_VALUE_TYPE)
    public FaceValueType getFaceValueType() {
        return faceValueType;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA)
    public Map<String, String> getData() {
        return data;
    }

    @JsonProperty(ENABLED)
    public Boolean getEnabled() {
        return enabled;
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
