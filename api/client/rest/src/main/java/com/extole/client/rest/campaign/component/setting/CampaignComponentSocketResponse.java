package com.extole.client.rest.campaign.component.setting;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.SocketDescriptionBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignComponentSocketResponse extends CampaignComponentSettingResponse {

    public static final String SETTING_TYPE = "MULTI_SOCKET";

    private static final String JSON_COMPONENT_SOCKET_FILTER = "filter";
    private static final String JSON_COMPONENT_SOCKET_PARAMETERS = "parameters";

    private final SocketFilterResponse filter;
    private final BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> description;
    private final List<CampaignComponentVariableResponse> parameters;

    @JsonCreator
    public CampaignComponentSocketResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION) BuildtimeEvaluatable<SocketDescriptionBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SOCKET_FILTER) SocketFilterResponse filter,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS) List<CampaignComponentVariableResponse> parameters) {
        super(name, displayName, type, description, tags, priority);
        this.filter = filter;
        this.parameters = parameters;
        this.description = description;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_FILTER)
    public SocketFilterResponse getFilter() {
        return filter;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION)
    @Override
    public BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS)
    public List<CampaignComponentVariableResponse> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
