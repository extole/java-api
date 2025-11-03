package com.extole.client.rest.reward.supplier.tango;

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
import com.extole.client.rest.reward.supplier.FaceValueAlgorithmType;
import com.extole.client.rest.reward.supplier.FaceValueType;
import com.extole.client.rest.reward.supplier.PartnerRewardKeyType;
import com.extole.client.rest.reward.supplier.RewardSupplierCreateRequest;
import com.extole.client.rest.reward.supplier.RewardSupplierType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class TangoRewardSupplierCreateRequest extends RewardSupplierCreateRequest {

    public static final String TANGO_REWARD_SUPPLIER_TYPE = "TANGO_V2";

    private static final String UTID = "utid";
    private static final String ACCOUNT_ID = "account_id";

    private final String utid;
    private final String accountId;

    public TangoRewardSupplierCreateRequest(
        @JsonProperty(UTID) String utid,
        @JsonProperty(ACCOUNT_ID) String accountId,
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
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<RewardSupplierBuildtimeContext, String>> name,
        @JsonProperty(DISPLAY_NAME) Omissible<
            BuildtimeEvaluatable<RewardSupplierBuildtimeContext, Optional<String>>> displayName,
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
        super(RewardSupplierType.TANGO_V2, name, displayName, faceValueAlgorithmType, faceValue, cashBackPercentage,
            minCashBack, maxCashBack, faceValueType, partnerRewardSupplierId, partnerRewardKeyType, displayType,
            description, limitPerDay, limitPerHour, componentIds, componentReferences, tags, data, enabled);
        this.utid = utid;
        this.accountId = accountId;
    }

    @JsonProperty(UTID)
    public String getUtid() {
        return utid;
    }

    @JsonProperty(ACCOUNT_ID)
    public String getAccountId() {
        return accountId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends RewardSupplierCreateRequest.Builder<TangoRewardSupplierCreateRequest, Builder> {
        private String utid;
        private String accountId;

        private Builder() {
        }

        public Builder withUtid(String utid) {
            this.utid = utid;
            return this;
        }

        public Builder withAccountIdentifier(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public TangoRewardSupplierCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new TangoRewardSupplierCreateRequest(
                utid,
                accountId,
                faceValueAlgorithmType,
                faceValue,
                cashBackPercentage,
                minCashBack,
                maxCashBack,
                faceValueType,
                partnerRewardSupplierId,
                partnerRewardKeyType,
                displayType,
                name,
                displayName,
                description,
                limitPerDay,
                limitPerHour,
                componentIds,
                componentReferences,
                tags,
                data,
                enabled);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
