package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentRewardSupplierIdListVariableConfiguration extends CampaignComponentVariableConfiguration {

    public static final String SETTING_TYPE_REWARD_SUPPLIER_ID_LIST = "REWARD_SUPPLIER_ID_LIST";

    private static final String JSON_ALLOWED_VALUES = "allowed_reward_supplier_ids";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>> allowedRewardSupplierIds;

    @JsonCreator
    public CampaignComponentRewardSupplierIdListVariableConfiguration(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION)
        BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_ALLOWED_VALUES) BuildtimeEvaluatable<CampaignBuildtimeContext,
            List<Id<?>>> allowedRewardSupplierIds) {
        super(name, displayName, type, values, source, description, tags, priority);
        this.allowedRewardSupplierIds = allowedRewardSupplierIds;
    }

    @JsonProperty(JSON_ALLOWED_VALUES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<Id<?>>> getAllowedRewardSupplierIds() {
        return allowedRewardSupplierIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
