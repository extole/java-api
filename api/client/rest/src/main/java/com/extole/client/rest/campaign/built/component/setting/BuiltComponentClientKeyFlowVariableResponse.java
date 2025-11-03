package com.extole.client.rest.campaign.built.component.setting;

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

public class BuiltComponentClientKeyFlowVariableResponse
    extends BuiltCampaignComponentVariableResponse {
    public static final String SETTING_TYPE = "CLIENT_KEY_FLOW";
    private static final String JSON_COMPONENT_VARIABLE_REDIRECT_URI = "redirect_uri";
    private static final String JSON_COMPONENT_VARIABLE_CLIENT_KEY_URL = "client_key_url";
    private static final String JSON_COMPONENT_VARIABLE_CLIENT_KEY_OAUTH_FLOW = "client_key_oauth_flow";

    private final String redirectUri;
    private final String clientKeyUrl;
    private final String clientKeyOAuthFlow;

    @JsonCreator
    public BuiltComponentClientKeyFlowVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID) Id<ComponentResponse> sourceComponentId,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_VERSION) Optional<Integer> sourceVersion,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_COMPONENT_VARIABLE_REDIRECT_URI) String redirectUri,
        @JsonProperty(JSON_COMPONENT_VARIABLE_CLIENT_KEY_URL) String clientKeyUrl,
        @JsonProperty(JSON_COMPONENT_VARIABLE_CLIENT_KEY_OAUTH_FLOW) String clientKeyOAuthFlow) {
        super(name, displayName, type, values, source, description, tags, sourceComponentId, sourceVersion, priority);
        this.redirectUri = redirectUri;
        this.clientKeyUrl = clientKeyUrl;
        this.clientKeyOAuthFlow = clientKeyOAuthFlow;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_REDIRECT_URI)
    public String getRedirectUri() {
        return redirectUri;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_CLIENT_KEY_URL)
    public String getClientKeyUrl() {
        return clientKeyUrl;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_CLIENT_KEY_OAUTH_FLOW)
    public String getClientKeyOAuthFlow() {
        return clientKeyOAuthFlow;
    }
}
