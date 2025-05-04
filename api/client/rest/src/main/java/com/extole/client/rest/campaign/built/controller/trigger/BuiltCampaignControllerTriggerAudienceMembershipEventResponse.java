package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventType;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerAudienceMembershipEventResponse
    extends BuiltCampaignControllerTriggerResponse {

    private static final String EVENT_TYPES = "event_types";
    private static final String AUDIENCE_IDS = "audience_ids";

    private final Set<CampaignControllerTriggerAudienceMembershipEventType> eventTypes;
    private final Set<Id<?>> audienceIds;

    public BuiltCampaignControllerTriggerAudienceMembershipEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(EVENT_TYPES) Set<CampaignControllerTriggerAudienceMembershipEventType> eventTypes,
        @JsonProperty(AUDIENCE_IDS) Set<Id<?>> audienceIds,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP_EVENT, triggerPhase, name,
            description, enabled, negated, componentIds, componentReferences);
        this.eventTypes = ImmutableSet.copyOf(eventTypes);
        this.audienceIds = ImmutableSet.copyOf(audienceIds);
    }

    @JsonProperty(EVENT_TYPES)
    public Set<CampaignControllerTriggerAudienceMembershipEventType> getEventTypes() {
        return eventTypes;
    }

    @JsonProperty(AUDIENCE_IDS)
    public Set<Id<?>> getAudienceIds() {
        return audienceIds;
    }

}
