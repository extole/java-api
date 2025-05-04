package com.extole.client.rest.campaign.built.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltComponentRewardSupplierIdListVariableResponse
    extends BuiltCampaignComponentVariableResponse {
    public static final String SETTING_TYPE = "REWARD_SUPPLIER_ID_LIST";
    private static final String ALLOWED_REWARD_SUPPLIER_IDS = "allowed_reward_supplier_ids";

    private final List<Id<?>> allowedRewardSupplierIds;

    @JsonCreator
    public BuiltComponentRewardSupplierIdListVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID) Id<ComponentResponse> sourceComponentId,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(ALLOWED_REWARD_SUPPLIER_IDS) List<Id<?>> allowedRewardSupplierIds) {
        super(name, displayName, type, values, source, description, tags, sourceComponentId, priority);
        this.allowedRewardSupplierIds = allowedRewardSupplierIds;
    }

    @JsonProperty(ALLOWED_REWARD_SUPPLIER_IDS)
    public List<Id<?>> getAllowedRewardSupplierIds() {
        return allowedRewardSupplierIds;
    }
}
