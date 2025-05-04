package com.extole.client.rest.campaign.built.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.SocketFilterResponse;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.RuntimeEvaluatable;

public class BuiltComponentSocketResponse extends BuiltCampaignComponentSettingResponse {

    static final String SETTING_TYPE = "MULTI_SOCKET";

    private static final String JSON_COMPONENT_SOCKET_FILTER = "filter";
    private static final String JSON_COMPONENT_SOCKET_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_SOCKET_PARAMETERS = "parameters";

    private final SocketFilterResponse filter;
    private final Optional<String> description;
    private final List<BuiltCampaignComponentVariableResponse> parameters;

    @JsonCreator
    public BuiltComponentSocketResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_SOCKET_FILTER) SocketFilterResponse filter,
        @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS) List<BuiltCampaignComponentVariableResponse> parameters) {
        super(name, displayName, type, values, tags, priority);
        this.filter = filter;
        this.description = description;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_FILTER)
    public SocketFilterResponse getFilter() {
        return filter;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS)
    public List<BuiltCampaignComponentVariableResponse> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
