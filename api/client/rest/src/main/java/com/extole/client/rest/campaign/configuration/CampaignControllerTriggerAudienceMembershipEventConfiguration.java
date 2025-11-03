package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerAudienceMembershipEventConfiguration
    extends CampaignControllerTriggerConfiguration {

    @Schema
    public enum EventType {
        CREATED,
        UPDATED,
        REMOVED
    }

    private static final String EVENT_TYPES = "event_types";
    private static final String AUDIENCE_IDS = "audience_ids";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<EventType>> eventTypes;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> audienceIds;

    public CampaignControllerTriggerAudienceMembershipEventConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(EVENT_TYPES) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<EventType>> eventTypes,
        @JsonProperty(AUDIENCE_IDS) BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> audienceIds,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId,
            CampaignControllerTriggerType.AUDIENCE_MEMBERSHIP_EVENT,
            triggerPhase,
            name,
            parentTriggerGroupName,
            description,
            enabled,
            negated,
            componentReferences);
        this.eventTypes = eventTypes;
        this.audienceIds = audienceIds;
    }

    @JsonProperty(EVENT_TYPES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<EventType>> getEventTypes() {
        return eventTypes;
    }

    @JsonProperty(AUDIENCE_IDS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> getAudienceIds() {
        return audienceIds;
    }

}
