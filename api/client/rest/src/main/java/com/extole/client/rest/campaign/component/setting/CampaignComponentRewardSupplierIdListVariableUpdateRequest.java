package com.extole.client.rest.campaign.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentRewardSupplierIdListVariableUpdateRequest
    extends CampaignComponentVariableUpdateRequest {

    static final String SETTING_TYPE = "REWARD_SUPPLIER_ID_LIST";

    private static final String JSON_ALLOWED_REWARD_SUPPLIER_IDS = "allowed_reward_supplier_ids";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>>> allowedRewardSupplierIds;

    @JsonCreator
    public CampaignComponentRewardSupplierIdListVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES)
        Omissible<Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION)
        Omissible<BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_ALLOWED_REWARD_SUPPLIER_IDS)
        Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>>> allowedRewardSupplierIds) {
        super(name, displayName, SettingType.REWARD_SUPPLIER_ID_LIST, values, source, description, tags, priority);
        this.allowedRewardSupplierIds = allowedRewardSupplierIds;
    }

    @JsonProperty(JSON_ALLOWED_REWARD_SUPPLIER_IDS)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>>> getAllowedRewardSupplierIds() {
        return allowedRewardSupplierIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends CampaignComponentVariableUpdateRequest.Builder<CALLER,
        CampaignComponentRewardSupplierIdListVariableUpdateRequest, Builder<CALLER, BUILDER_TYPE>> {

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>>> allowedRewardSupplierIds =
            Omissible.omitted();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public Builder withAllowedRewardSupplierIds(
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>> rewardSupplierIdList) {
            this.allowedRewardSupplierIds = Omissible.of(rewardSupplierIdList);
            return this;
        }

        public CampaignComponentRewardSupplierIdListVariableUpdateRequest build() {
            return new CampaignComponentRewardSupplierIdListVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority,
                allowedRewardSupplierIds);
        }

    }

}
