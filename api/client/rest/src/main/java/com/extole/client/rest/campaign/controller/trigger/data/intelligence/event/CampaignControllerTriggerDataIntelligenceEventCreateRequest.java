package com.extole.client.rest.campaign.controller.trigger.data.intelligence.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerDataIntelligenceEventCreateRequest extends
    CampaignControllerTriggerRequest {

    private static final String EVENT_NAME = "event_name";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName;

    public CampaignControllerTriggerDataIntelligenceEventCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(EVENT_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.eventName = eventName;
    }

    @JsonProperty(EVENT_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getEventName() {
        return eventName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName;

        private Builder() {
        }

        public Builder withEventName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventName = Provided.of(eventName);
            return this;
        }

        public CampaignControllerTriggerDataIntelligenceEventCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerDataIntelligenceEventCreateRequest(
                triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                eventName,
                componentIds,
                componentReferences);
        }

    }

}
