package com.extole.client.rest.campaign.component.setting;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.api.campaign.SocketDescriptionBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

public class CampaignComponentSocketUpdateRequest extends CampaignComponentSettingUpdateRequest {

    static final String SETTING_TYPE = "MULTI_SOCKET";

    private static final String JSON_COMPONENT_SOCKET_FILTER = "filter";
    private static final String JSON_COMPONENT_SOCKET_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_SOCKET_PARAMETERS = "parameters";

    private final Omissible<SocketFilterUpdateRequest> filter;
    private final Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description;
    private final Omissible<List<CampaignComponentVariableRequest>> parameters;

    @JsonCreator
    public CampaignComponentSocketUpdateRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SOCKET_FILTER) Omissible<SocketFilterUpdateRequest> filter,
        @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS) Omissible<
            List<CampaignComponentVariableRequest>> parameters) {
        super(name, displayName, type, tags, priority);
        this.filter = filter;
        this.description = description;
        this.parameters = parameters;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_FILTER)
    public Omissible<SocketFilterUpdateRequest> getFilter() {
        return filter;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_SOCKET_PARAMETERS)
    public Omissible<List<CampaignComponentVariableRequest>> getParameters() {
        return parameters;
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

    public static final class Builder<CALLER, RESULT extends CampaignComponentSocketUpdateRequest,
        BUILDER_TYPE extends Builder<CALLER, RESULT, BUILDER_TYPE>>
        extends CampaignComponentSettingUpdateRequest.Builder<CALLER, RESULT, BUILDER_TYPE> {

        private Omissible<SocketFilterUpdateRequest> filter = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private List<CampaignComponentVariableRequest.Builder> parameterBuilders = Lists.newArrayList();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withFilter(SocketFilterUpdateRequest filter) {
            this.filter = Omissible.of(filter);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE
            withDescription(BuildtimeEvaluatable<SocketDescriptionBuildtimeContext, Optional<String>> description) {
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

        public RESULT build() {
            Omissible<List<CampaignComponentVariableRequest>> parameters;
            if (parameterBuilders.isEmpty()) {
                parameters = Omissible.omitted();
            } else {
                parameters = Omissible.of(parameterBuilders.stream().map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return (RESULT) new CampaignComponentSocketUpdateRequest(
                name,
                displayName,
                type,
                filter,
                description,
                tags,
                priority,
                parameters);
        }

    }

}
