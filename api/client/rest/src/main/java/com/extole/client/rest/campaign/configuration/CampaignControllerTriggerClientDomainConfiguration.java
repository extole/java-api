package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerClientDomainConfiguration extends CampaignControllerTriggerConfiguration {

    private static final String CLIENT_DOMAIN_IDS = "client_domain_ids";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> clientDomainIds;

    public CampaignControllerTriggerClientDomainConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(CLIENT_DOMAIN_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> clientDomainIds,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.CLIENT_DOMAIN, triggerPhase, name, description, enabled, negated,
            componentReferences);
        this.clientDomainIds = clientDomainIds;
    }

    @JsonProperty(CLIENT_DOMAIN_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getClientDomainIds() {
        return clientDomainIds;
    }

}
