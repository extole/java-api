package com.extole.client.rest.campaign.flow.step.app;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignFlowStepAppCreateRequest extends ComponentElementRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TYPE = "type";

    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description;
    private final CampaignFlowStepAppTypeCreateRequest type;

    @JsonCreator
    public CampaignFlowStepAppCreateRequest(
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_TYPE) CampaignFlowStepAppTypeCreateRequest type,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_TYPE)
    public CampaignFlowStepAppTypeCreateRequest getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private CampaignFlowStepAppTypeCreateRequest.Builder<Builder> typeBuilder;

        private Builder() {

        }

        public Builder withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = name;
            return this;
        }

        public Builder
            withDescription(BuildtimeEvaluatable<CampaignBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public CampaignFlowStepAppTypeCreateRequest.Builder<Builder> withType() {
            CampaignFlowStepAppTypeCreateRequest.Builder<Builder> builder =
                CampaignFlowStepAppTypeCreateRequest.builder(this);
            this.typeBuilder = builder;
            return builder;
        }

        public CampaignFlowStepAppCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignFlowStepAppCreateRequest(name,
                description,
                typeBuilder == null ? null : typeBuilder.build(),
                componentIds,
                componentReferences);
        }
    }
}
