package com.extole.client.rest.reward.supplier.tango.built;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
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

public class BuiltTangoRewardSupplierResponse extends BuiltRewardSupplierResponse {

    public static final String TANGO_REWARD_SUPPLIER_TYPE = "TANGO_V2";

    private static final String UTID = "utid";
    private static final String ACCOUNT_ID = "account_id";
    private static final String BRAND_NAME = "brand_name";
    private static final String BRAND_DESCRIPTION = "brand_description";
    private static final String BRAND_DISCLAIMER = "brand_disclaimer";
    private static final String BRAND_IMAGE_URL = "brand_image_url";
    private static final String DESCRIPTION = "description";

    private final String id;
    private final String utid;
    private final String accountId;
    private final String brandName;
    private final String brandDescription;
    private final String brandDisclaimer;
    private final String brandImageUrl;
    private final Optional<String> description;

    @JsonCreator
    public BuiltTangoRewardSupplierResponse(
        @JsonProperty(REWARD_SUPPLIER_ID) String id,
        @JsonProperty(PARTNER_REWARD_SUPPLIER_ID) Optional<String> partnerRewardSupplierId,
        @JsonProperty(PARTNER_REWARD_KEY_TYPE) PartnerRewardKeyType partnerRewardKeyType,
        @JsonProperty(DISPLAY_TYPE) String displayType,
        @JsonProperty(UTID) String utid,
        @JsonProperty(ACCOUNT_ID) String accountId,
        @JsonProperty(FACE_VALUE_ALGORITHM_TYPE) FaceValueAlgorithmType faceValueAlgorithmType,
        @JsonProperty(FACE_VALUE) BigDecimal faceValue,
        @JsonProperty(CASH_BACK_PERCENTAGE) BigDecimal cashBackPercentage,
        @JsonProperty(CASH_BACK_MIN) BigDecimal minCashBack,
        @JsonProperty(CASH_BACK_MAX) BigDecimal maxCashBack,
        @JsonProperty(LIMIT_PER_DAY) Optional<Integer> limitPerDay,
        @JsonProperty(LIMIT_PER_HOUR) Optional<Integer> limitPerHour,
        @JsonProperty(FACE_VALUE_TYPE) FaceValueType faceValueType,
        @JsonProperty(NAME) String name,
        @JsonProperty(BRAND_NAME) String brandName,
        @JsonProperty(BRAND_DESCRIPTION) String brandDescription,
        @JsonProperty(BRAND_DISCLAIMER) String brandDisclaimer,
        @JsonProperty(BRAND_IMAGE_URL) String brandImageUrl,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(DATA) Map<String, String> data,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(STATE_TRANSITIONS) Map<RewardState, List<RewardState>> stateTransitions) {
        super(RewardSupplierType.TANGO_V2, id, partnerRewardSupplierId, partnerRewardKeyType, displayType, name,
            faceValueAlgorithmType, faceValue, cashBackPercentage, minCashBack, maxCashBack, limitPerDay, limitPerHour,
            faceValueType, createdDate, updatedDate, componentIds, componentReferences, tags, data, enabled,
            stateTransitions);
        this.id = id;
        this.utid = utid;
        this.accountId = accountId;
        this.brandName = brandName;
        this.brandDescription = brandDescription;
        this.brandDisclaimer = brandDisclaimer;
        this.brandImageUrl = brandImageUrl;
        this.description = description;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(UTID)
    public String getUtid() {
        return utid;
    }

    @JsonProperty(ACCOUNT_ID)
    public String getAccountId() {
        return accountId;
    }

    @JsonProperty(BRAND_NAME)
    public String getBrandName() {
        return brandName;
    }

    @JsonProperty(BRAND_DESCRIPTION)
    public String getBrandDescription() {
        return brandDescription;
    }

    @JsonProperty(BRAND_DISCLAIMER)
    public String getBrandDisclaimer() {
        return brandDisclaimer;
    }

    @JsonProperty(BRAND_IMAGE_URL)
    public String getBrandImageUrl() {
        return brandImageUrl;
    }

    @JsonProperty(DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
