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

public class CampaignComponentComponentIdVariableUpdateRequest extends CampaignComponentVariableUpdateRequest {
    static final String SETTING_TYPE = "COMPONENT_ID";

    private static final String JSON_FILTER = "filter";

    private final Omissible<ComponentIdFilterUpdateRequest> filter;

    @JsonCreator
    public CampaignComponentComponentIdVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_FILTER) Omissible<ComponentIdFilterUpdateRequest> filter) {
        super(name, displayName, SettingType.COMPONENT_ID, values, source, description, tags, priority);
        this.filter = filter;
    }

    @JsonProperty(JSON_FILTER)
    public Omissible<ComponentIdFilterUpdateRequest> getFilter() {
        return filter;
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends
        CampaignComponentVariableUpdateRequest.Builder<CALLER, CampaignComponentComponentIdVariableUpdateRequest,
            Builder<CALLER, BUILDER_TYPE>> {

        private Omissible<ComponentIdFilterUpdateRequest> filter = Omissible.omitted();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withFilter(ComponentIdFilterUpdateRequest filter) {
            this.filter = Omissible.of(filter);
            return (BUILDER_TYPE) this;
        }

        @Override
        public CampaignComponentComponentIdVariableUpdateRequest build() {
            return new CampaignComponentComponentIdVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority,
                filter);
        }
    }
}
