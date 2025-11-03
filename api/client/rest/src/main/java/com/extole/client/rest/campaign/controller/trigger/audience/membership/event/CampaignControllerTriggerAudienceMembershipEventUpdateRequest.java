package com.extole.client.rest.campaign.controller.trigger.audience.membership.event;

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

public final class CampaignControllerTriggerAudienceMembershipEventUpdateRequest extends
    CampaignControllerTriggerRequest {

    private static final String EVENT_TYPES = "event_types";
    private static final String AUDIENCE_IDS = "audience_ids";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        Set<CampaignControllerTriggerAudienceMembershipEventType>>> eventTypes;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> audienceIds;

    @JsonCreator
    public CampaignControllerTriggerAudienceMembershipEventUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(EVENT_TYPES) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<CampaignControllerTriggerAudienceMembershipEventType>>> eventTypes,
        @JsonProperty(AUDIENCE_IDS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> audienceIds,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.eventTypes = eventTypes;
        this.audienceIds = audienceIds;
    }

    @JsonProperty(EVENT_TYPES)
    public
        Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<CampaignControllerTriggerAudienceMembershipEventType>>>
        getEventTypes() {
        return eventTypes;
    }

    @JsonProperty(AUDIENCE_IDS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> getAudienceIds() {
        return audienceIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            Set<CampaignControllerTriggerAudienceMembershipEventType>>> eventTypes =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>>> audienceIds =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withEventTypes(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                Set<CampaignControllerTriggerAudienceMembershipEventType>> eventTypes) {
            this.eventTypes = Omissible.of(eventTypes);
            return this;
        }

        public Builder withAudienceIds(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<Id<?>>> audienceIds) {
            this.audienceIds = Omissible.of(audienceIds);
            return this;
        }

        public CampaignControllerTriggerAudienceMembershipEventUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerAudienceMembershipEventUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                eventTypes,
                audienceIds,
                componentIds,
                componentReferences);
        }
    }
}
