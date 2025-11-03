package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentPartnerEnumListVariableConfiguration extends CampaignComponentVariableConfiguration {

    public static final String SETTING_TYPE_PARTNER = "PARTNER_ENUM_LIST";

    private static final String JSON_WEBHOOK_ID = "webhook_id";
    private static final String JSON_OPTIONS = "options";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<Id<?>>> webhookId;
    private final List<PartnerEnumListVariableOptionConfiguration> options;

    @JsonCreator
    public CampaignComponentPartnerEnumListVariableConfiguration(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) VariableSource source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) BuildtimeEvaluatable<VariableDescriptionBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority,
        @JsonProperty(JSON_WEBHOOK_ID) BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<Id<?>>> webhookId,
        @JsonProperty(JSON_OPTIONS) List<PartnerEnumListVariableOptionConfiguration> options) {
        super(name, displayName, type, values, source, description, tags, priority);
        this.webhookId = webhookId;
        this.options = options == null ? ImmutableList.of() : ImmutableList.copyOf(options);
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<Id<?>>> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_OPTIONS)
    public List<PartnerEnumListVariableOptionConfiguration> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
