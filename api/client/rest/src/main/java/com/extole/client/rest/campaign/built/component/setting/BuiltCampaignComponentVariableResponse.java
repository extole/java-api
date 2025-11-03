package com.extole.client.rest.campaign.built.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignComponentVariableResponse extends BuiltCampaignComponentSettingResponse {

    protected static final String JSON_COMPONENT_VARIABLE_SOURCE = "source";
    protected static final String JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID = "source_component_id";
    protected static final String JSON_COMPONENT_VARIABLE_SOURCE_VERSION = "source_version";
    protected static final String JSON_COMPONENT_VARIABLE_DESCRIPTION = "description";

    private final VariableSource source;
    private final Id<ComponentResponse> sourceComponentId;
    private final Optional<Integer> sourceVersion;
    private final Optional<String> description;

    @JsonCreator
    public BuiltCampaignComponentVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID) Id<ComponentResponse> sourceComponentId,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_VERSION) Optional<Integer> sourceVersion,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority) {
        super(name, displayName, type, values, tags, priority);
        this.source = source;
        this.sourceComponentId = sourceComponentId;
        this.sourceVersion = sourceVersion;
        this.description = description;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE)
    public VariableSource getSource() {
        return source;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID)
    public Id<ComponentResponse> getSourceComponentId() {
        return sourceComponentId;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_VERSION)
    public Optional<Integer> getSourceVersion() {
        return sourceVersion;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
