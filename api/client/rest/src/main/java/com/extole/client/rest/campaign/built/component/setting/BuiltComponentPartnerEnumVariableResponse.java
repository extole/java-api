package com.extole.client.rest.campaign.built.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.setting.PartnerEnumListVariableOptionResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltComponentPartnerEnumVariableResponse extends BuiltCampaignComponentVariableResponse {
    public static final String SETTING_TYPE = "PARTNER_ENUM";

    private static final String JSON_WEBHOOK_ID = "webhook_id";
    private static final String JSON_OPTIONS = "options";

    private final Optional<String> webhookId;
    private final List<PartnerEnumListVariableOptionResponse> options;

    @JsonCreator
    public BuiltComponentPartnerEnumVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Optional<String> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_COMPONENT_ID) Id<ComponentResponse> sourceComponentId,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE_VERSION) Optional<Integer> sourceVersion,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_WEBHOOK_ID) Optional<String> webhookId,
        @JsonProperty(JSON_OPTIONS) List<PartnerEnumListVariableOptionResponse> options) {
        super(name, displayName, type, values, source, description, tags, sourceComponentId, sourceVersion, priority);
        this.webhookId = webhookId;
        this.options = options;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public Optional<String> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_OPTIONS)
    public List<PartnerEnumListVariableOptionResponse> getOptions() {
        return options;
    }
}
