package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.SocketDescriptionBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignComponentSocketConfiguration extends CampaignComponentSettingConfiguration {

    public static final String SETTING_TYPE_MULTI_SOCKET = "MULTI_SOCKET";

    private static final String JSON_FILTER = "filter";
    private static final String JSON_PARAMETERS = "parameters";
    private static final String JSON_DESCRIPTION = "description";

    private final CampaignComponentSocketFilterConfiguration filter;
    private final BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> description;
    private final List<CampaignComponentVariableConfiguration> parameters;

    @JsonCreator
    public CampaignComponentSocketConfiguration(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_FILTER) CampaignComponentSocketFilterConfiguration filter,
        @JsonProperty(JSON_DESCRIPTION) BuildtimeEvaluatable<SocketDescriptionBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_PARAMETERS) List<CampaignComponentVariableConfiguration> parameters) {
        super(name, displayName, type, tags, priority);
        this.filter = filter;
        this.description = description;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_FILTER)
    public CampaignComponentSocketFilterConfiguration getFilter() {
        return filter;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_PARAMETERS)
    public List<CampaignComponentVariableConfiguration> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
