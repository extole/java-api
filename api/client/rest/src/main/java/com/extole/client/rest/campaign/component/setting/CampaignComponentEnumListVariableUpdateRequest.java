package com.extole.client.rest.campaign.component.setting;

import java.util.List;
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

public final class CampaignComponentEnumListVariableUpdateRequest extends CampaignComponentVariableUpdateRequest {
    static final String SETTING_TYPE = "ENUM_LIST";

    private static final String JSON_ALLOWED_VALUES = "allowed_values";

    private final Omissible<List<String>> allowedValues;

    @JsonCreator
    private CampaignComponentEnumListVariableUpdateRequest(
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
        @JsonProperty(JSON_ALLOWED_VALUES) Omissible<List<String>> allowedValues) {
        super(name, displayName, SettingType.ENUM_LIST, values, source, description, tags, priority);
        this.allowedValues = allowedValues;
    }

    @JsonProperty(JSON_ALLOWED_VALUES)
    public Omissible<List<String>> getAllowedValues() {
        return allowedValues;
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends CampaignComponentVariableUpdateRequest.Builder<CALLER, CampaignComponentEnumListVariableUpdateRequest,
            Builder<CALLER, BUILDER_TYPE>> {

        private Omissible<List<String>> allowedValues = Omissible.omitted();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withAllowedValues(List<String> allowedValues) {
            this.allowedValues = Omissible.of(allowedValues);
            return (BUILDER_TYPE) this;
        }

        @Override
        public CampaignComponentEnumListVariableUpdateRequest build() {
            return new CampaignComponentEnumListVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority,
                allowedValues);
        }

    }

}
