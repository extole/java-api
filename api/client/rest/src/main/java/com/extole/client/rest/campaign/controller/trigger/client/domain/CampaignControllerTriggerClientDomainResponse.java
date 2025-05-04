package com.extole.client.rest.campaign.controller.trigger.client.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerClientDomainResponse extends CampaignControllerTriggerResponse {

    private static final String CLIENT_DOMAIN_IDS = "client_domain_ids";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> clientDomainIds;

    public CampaignControllerTriggerClientDomainResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(CLIENT_DOMAIN_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> clientDomainIds,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.CLIENT_DOMAIN, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.clientDomainIds = clientDomainIds;
    }

    @JsonProperty(CLIENT_DOMAIN_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getClientDomainIds() {
        return clientDomainIds;
    }

}
