package com.extole.client.rest.campaign.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class CampaignComponentVariableResponse extends CampaignComponentSettingResponse {

    protected static final String JSON_COMPONENT_VARIABLE_VALUES = "values";
    protected static final String JSON_COMPONENT_VARIABLE_SOURCE = "source";

    private final Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
        RuntimeEvaluatable<Object, Optional<Object>>>> values;
    private final VariableSource source;
    private final BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> description;

    @JsonCreator
    public CampaignComponentVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION) BuildtimeEvaluatable<VariableDescriptionBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority) {
        super(name, displayName, type, description, tags, priority);
        this.values = values;
        this.source = source;
        this.description = description;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES)
    public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
        RuntimeEvaluatable<Object, Optional<Object>>>> getValues() {
        return values;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE)
    public VariableSource getSource() {
        return source;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION)
    public BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
