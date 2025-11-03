package com.extole.client.rest.campaign.controller.trigger.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerEventCreateRequest extends CampaignControllerTriggerRequest {
    private static final String EVENT_NAMES = "event_names";
    private static final String EVENT_TYPE = "event_type";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> eventType;

    @JsonCreator
    public CampaignControllerTriggerEventCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(EVENT_NAMES) BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames,
        @JsonProperty(EVENT_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerEventType> eventType,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.eventNames = eventNames;
        this.eventType = eventType;
    }

    @JsonProperty(EVENT_NAMES)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> getEventNames() {
        return eventNames;
    }

    @JsonProperty(EVENT_TYPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> getEventType() {
        return eventType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {
        private BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames;
        private BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> eventType;

        private Builder() {
        }

        public Builder withEventNames(BuildtimeEvaluatable<ControllerBuildtimeContext, List<String>> eventNames) {
            this.eventNames = eventNames;
            return this;
        }

        public Builder withEventNames(List<String> eventNames) {
            this.eventNames = Provided.of(ImmutableList.copyOf(eventNames));
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventNames = Provided.of(ImmutableList.of(eventName));
            return this;
        }

        public Builder withEventType(CampaignControllerTriggerEventType eventType) {
            this.eventType = Provided.of(eventType);
            return this;
        }

        public Builder withEventType(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerEventType> eventType) {
            this.eventType = eventType;
            return this;
        }

        @Override
        public CampaignControllerTriggerEventCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerEventCreateRequest(
                triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                eventNames,
                eventType,
                componentIds,
                componentReferences);
        }

    }

}
