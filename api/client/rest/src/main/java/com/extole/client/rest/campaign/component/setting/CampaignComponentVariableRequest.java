package com.extole.client.rest.campaign.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public class CampaignComponentVariableRequest extends CampaignComponentSettingRequest {

    protected static final String JSON_COMPONENT_VARIABLE_VALUES = "values";
    protected static final String JSON_COMPONENT_VARIABLE_SOURCE = "source";
    protected static final String JSON_COMPONENT_SETTING_DESCRIPTION = "description";

    private final Omissible<Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
        RuntimeEvaluatable<Object, Optional<Object>>>>> values;
    private final Omissible<VariableSource> source;
    private final Omissible<BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description;

    @JsonCreator
    protected CampaignComponentVariableRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority) {
        super(name, displayName, type, tags, priority);
        this.values = values;
        this.source = source;
        this.description = description;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES)
    public
        Omissible<Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>>>
        getValues() {
        return values;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE)
    public Omissible<VariableSource> getSource() {
        return source;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder<?, ?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static class Builder<CALLER, RESULT extends CampaignComponentVariableRequest,
        BUILDER_TYPE extends Builder<CALLER, RESULT, BUILDER_TYPE>>
        extends CampaignComponentSettingRequest.Builder<CALLER, RESULT, BUILDER_TYPE> {

        protected Omissible<Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>>> values = Omissible.omitted();
        protected Omissible<VariableSource> source = Omissible.omitted();
        protected Omissible<BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();

        protected Builder() {
            super();
        }

        protected Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withValues(Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values) {
            this.values = Omissible.of(values);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withSource(VariableSource source) {
            this.source = Omissible.of(source);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE
            withDescription(BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return (BUILDER_TYPE) this;
        }

        public RESULT build() {
            return (RESULT) new CampaignComponentVariableRequest(
                name,
                displayName,
                type,
                values,
                source,
                description,
                tags,
                priority);
        }

    }

}
