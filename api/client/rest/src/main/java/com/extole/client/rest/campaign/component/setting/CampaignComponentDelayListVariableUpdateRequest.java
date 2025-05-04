package com.extole.client.rest.campaign.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public final class CampaignComponentDelayListVariableUpdateRequest extends CampaignComponentVariableUpdateRequest {
    static final String SETTING_TYPE = "DELAY_LIST";

    @JsonCreator
    public CampaignComponentDelayListVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority) {
        super(name, displayName, SettingType.DELAY_LIST, values, source, description, tags, priority);
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends CampaignComponentVariableUpdateRequest.Builder<CALLER, CampaignComponentDelayListVariableUpdateRequest,
            Builder<CALLER, BUILDER_TYPE>> {

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        @Override
        public CampaignComponentDelayListVariableUpdateRequest build() {
            return new CampaignComponentDelayListVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority);
        }

    }

}
