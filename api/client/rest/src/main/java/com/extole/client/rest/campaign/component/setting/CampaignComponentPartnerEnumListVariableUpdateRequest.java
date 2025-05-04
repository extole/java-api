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
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignComponentPartnerEnumListVariableUpdateRequest
    extends CampaignComponentVariableUpdateRequest {
    public static final String SETTING_TYPE = "PARTNER_ENUM_LIST";

    private static final String JSON_WEBHOOK_ID = "webhook_id";
    private static final String JSON_OPTIONS = "options";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>>> webhookId;
    private final Omissible<List<PartnerEnumListVariableOptionCreateRequest>> options;

    @JsonCreator
    private CampaignComponentPartnerEnumListVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_WEBHOOK_ID) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>>> webhookId,
        @JsonProperty(JSON_OPTIONS) Omissible<List<PartnerEnumListVariableOptionCreateRequest>> options) {
        super(name, displayName, SettingType.PARTNER_ENUM_LIST, values, source, description, tags, priority);
        this.webhookId = webhookId;
        this.options = options;
    }

    @JsonProperty(JSON_WEBHOOK_ID)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>>> getWebhookId() {
        return webhookId;
    }

    @JsonProperty(JSON_OPTIONS)
    public Omissible<List<PartnerEnumListVariableOptionCreateRequest>> getOptions() {
        return options;
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends CampaignComponentVariableUpdateRequest.Builder<CALLER,
        CampaignComponentPartnerEnumListVariableUpdateRequest,
        Builder<CALLER, BUILDER_TYPE>> {

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>>> webhookId = Omissible.omitted();
        private Omissible<List<PartnerEnumListVariableOptionCreateRequest>> options = Omissible.omitted();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withWebhookId(BuildtimeEvaluatable<CampaignBuildtimeContext, Id<?>> webhookId) {
            this.webhookId = Omissible.of(webhookId);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withOptions(List<PartnerEnumListVariableOptionCreateRequest> options) {
            this.options = Omissible.of(options);
            return (BUILDER_TYPE) this;
        }

        public CampaignComponentPartnerEnumListVariableUpdateRequest build() {
            return new CampaignComponentPartnerEnumListVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority,
                webhookId,
                options);
        }
    }

}
