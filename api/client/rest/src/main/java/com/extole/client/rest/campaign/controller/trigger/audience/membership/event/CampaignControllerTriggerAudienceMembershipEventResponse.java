package com.extole.client.rest.campaign.controller.trigger.audience.membership.event;

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

public class CampaignControllerTriggerAudienceMembershipEventResponse extends CampaignControllerTriggerResponse {

    private static final String EVENT_TYPES = "event_types";
    private static final String AUDIENCE_IDS = "audience_ids";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        Set<CampaignControllerTriggerAudienceMembershipEventType>> eventTypes;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> audienceIds;

    public CampaignControllerTriggerAudienceMembershipEventResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(EVENT_TYPES) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<CampaignControllerTriggerAudienceMembershipEventType>> eventTypes,
        @JsonProperty(AUDIENCE_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> audienceIds,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP_EVENT, triggerPhase, name,
            parentTriggerGroupName, description, enabled, negated, componentIds, componentReferences);
        this.eventTypes = eventTypes;
        this.audienceIds = audienceIds;
    }

    @JsonProperty(EVENT_TYPES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<CampaignControllerTriggerAudienceMembershipEventType>>
        getEventTypes() {
        return eventTypes;
    }

    @JsonProperty(AUDIENCE_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getAudienceIds() {
        return audienceIds;
    }

}
