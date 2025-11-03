package com.extole.client.rest.campaign.configuration;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class CampaignComponentComponentIdVariableConfiguration extends CampaignComponentVariableConfiguration {

    public static final String SETTING_TYPE_COMPONENT_REFERENCE = "COMPONENT_ID";

    private static final String JSON_FILTER = "filter";

    private final CampaignComponentComponentIdFilterConfiguration filter;

    @JsonCreator
    public CampaignComponentComponentIdVariableConfiguration(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) BuildtimeEvaluatable<VariableDescriptionBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_FILTER) CampaignComponentComponentIdFilterConfiguration filter) {
        super(name, displayName, type, values, source, description, tags, priority);
        this.filter = filter;
    }

    @JsonProperty(JSON_FILTER)
    public CampaignComponentComponentIdFilterConfiguration getFilter() {
        return filter;
    }

}
