package com.extole.client.rest.campaign.controller.trigger.client.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerTriggerClientDomainCreateRequest extends CampaignControllerTriggerRequest {

    private static final String CLIENT_DOMAIN_IDS = "client_domain_ids";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> clientDomainIds;

    @JsonCreator
    private CampaignControllerTriggerClientDomainCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(CLIENT_DOMAIN_IDS) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> clientDomainIds,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.clientDomainIds = clientDomainIds;
    }

    @JsonProperty(CLIENT_DOMAIN_IDS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> getClientDomainIds() {
        return clientDomainIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> clientDomainIds =
            Omissible.omitted();

        private Builder() {
        }

        public Builder
            withClientDomainIds(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> clientDomainIds) {
            this.clientDomainIds = Omissible.of(clientDomainIds);
            return this;
        }

        public CampaignControllerTriggerClientDomainCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerClientDomainCreateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                clientDomainIds,
                componentIds,
                componentReferences);
        }
    }

}
