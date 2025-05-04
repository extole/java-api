package com.extole.client.rest.campaign.component.setting;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentPartnerEnumListVariableResponse extends CampaignComponentVariableResponse {
    public static final String SETTING_TYPE = "PARTNER_ENUM_LIST";

    private static final String JSON_WEBHOOK_ID = "webhook_id";
    private static final String JSON_OPTIONS = "options";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>> webhookId;
    private final List<PartnerEnumListVariableOptionResponse> options;

    @JsonCreator
    public CampaignComponentPartnerEnumListVariableResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION)
        BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_WEBHOOK_ID) BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>> webhookId,
        @JsonProperty(JSON_OPTIONS) List<PartnerEnumListVariableOptionResponse> options) {
        super(name, displayName, type, values, source, description, tags, priority);
        this.webhookId = webhookId;
        this.options = options;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_OPTIONS)
    public List<PartnerEnumListVariableOptionResponse> getOptions() {
        return options;
    }
}
