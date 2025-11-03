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

public final class CampaignComponentEnumVariableRequest extends CampaignComponentVariableRequest {

    public static final String SETTING_TYPE = "ENUM";
    private static final String JSON_COMPONENT_VARIABLE_TYPE = "type";

    private static final String JSON_ALLOWED_VALUES = "allowed_values";

    private final Omissible<List<String>> allowedValues;

    @JsonCreator
    private CampaignComponentEnumVariableRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_ALLOWED_VALUES) Omissible<List<String>> allowedValues) {
        super(name, displayName, SettingType.ENUM, values, source, description, tags, priority);
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
        extends
        CampaignComponentVariableRequest.Builder<CALLER, CampaignComponentEnumVariableRequest,
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
        public CampaignComponentEnumVariableRequest build() {
            return new CampaignComponentEnumVariableRequest(
                name,
                displayName,
                type,
                values,
                source,
                description,
                tags,
                priority,
                allowedValues);
        }

    }

}
