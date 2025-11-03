package com.extole.client.rest.campaign.component.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.extole.api.campaign.SocketDescriptionBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignComponentSocketRequest extends CampaignComponentSettingRequest {

    static final String MULTI_SOCKET_SETTING_TYPE = "MULTI_SOCKET";
    static final String SOCKET_SETTING_TYPE = "SOCKET";

    private static final String JSON_COMPONENT_SOCKET_FILTERS = "filters";
    private static final String JSON_COMPONENT_SOCKET_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_SOCKET_PARAMETERS = "parameters";

    private final Omissible<List<SocketFilterCreateRequest>> filters;
    private final Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description;
    private final Omissible<List<? extends CampaignComponentVariableRequest>> parameters;

    @JsonCreator
    public CampaignComponentSocketRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SOCKET_FILTERS) Omissible<List<SocketFilterCreateRequest>> filters,
        @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS) Omissible<
            List<? extends CampaignComponentVariableRequest>> parameters) {
        super(name, displayName, type, tags, priority);
        this.filters = filters;
        this.description = description;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_FILTERS)
    public Omissible<List<SocketFilterCreateRequest>> getFilters() {
        return filters;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS)
    public Omissible<List<? extends CampaignComponentVariableRequest>> getParameters() {
        return parameters;
    }

    public static Builder<?, ?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, RESULT extends CampaignComponentSocketRequest, BUILDER_TYPE extends Builder<
        CALLER, RESULT, BUILDER_TYPE>>
        extends CampaignComponentSettingRequest.Builder<CALLER, RESULT, BUILDER_TYPE> {

        private Omissible<List<SocketFilterCreateRequest>> filters = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private final List<CampaignComponentVariableRequest.Builder<Builder, ?, ?>> parameterBuilders =
            Lists.newArrayList();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withFilters(SocketFilterCreateRequest... filters) {
            this.filters = Omissible.of(ImmutableList.copyOf(filters));
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withFilters(List<? extends SocketFilterCreateRequest> filters) {
            this.filters = Omissible.of(new ArrayList<>(filters));
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withDescription(
            BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return (BUILDER_TYPE) this;
        }

        public CampaignComponentVariableRequest.Builder<Builder, ?, ?> addParameter() {
            CampaignComponentVariableRequest.Builder<Builder, ?, ?> builder =
                CampaignComponentVariableRequest.builder(this);
            builder.withSettingType(SettingType.STRING);
            this.parameterBuilders.add(builder);
            return builder;
        }

        public <T extends CampaignComponentVariableRequest.Builder> T addParameterBuilder(T builder) {
            this.parameterBuilders.add(builder);
            return builder;
        }

        @Override
        public RESULT build() {
            Omissible<List<? extends CampaignComponentVariableRequest>> parameters;
            if (parameterBuilders.isEmpty()) {
                parameters = Omissible.omitted();
            } else {
                parameters = Omissible.of(parameterBuilders.stream().map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return (RESULT) new CampaignComponentSocketRequest(
                name,
                displayName,
                type,
                filters,
                description,
                tags,
                priority,
                parameters);
        }

    }

}
