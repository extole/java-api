package com.extole.client.rest.campaign.controller.trigger.step.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerStepEventUpdateRequest extends CampaignControllerTriggerRequest {

    private static final String EVENT_NAMES = "event_names";
    private static final String HAVING_ANY_DATA_NAME = "having_any_data_name";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> havingAnyDataName;

    public CampaignControllerTriggerStepEventUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(EVENT_NAMES) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames,
        @JsonProperty(HAVING_ANY_DATA_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> havingAnyDataName,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.eventNames = eventNames;
        this.havingAnyDataName = havingAnyDataName;
    }

    @JsonProperty(EVENT_NAMES)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getEventNames() {
        return eventNames;
    }

    @JsonProperty(HAVING_ANY_DATA_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> getHavingAnyDataName() {
        return havingAnyDataName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> eventNames =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>>> havingAnyDataName =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withEventNames(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> eventNames) {
            this.eventNames = Omissible.of(eventNames);
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventNames = Omissible.of(Provided.of(ImmutableSet.of(eventName)));
            return this;
        }

        public Builder
            withHavingAnyDataName(BuildtimeEvaluatable<ControllerBuildtimeContext, Set<String>> havingAnyDataName) {
            this.havingAnyDataName = Omissible.of(havingAnyDataName);
            return this;
        }

        public Builder withHavingDataName(String dataName) {
            this.havingAnyDataName = Omissible.of(Provided.of(ImmutableSet.of(dataName)));
            return this;
        }

        @Override
        public CampaignControllerTriggerStepEventUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerStepEventUpdateRequest(
                triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                eventNames,
                havingAnyDataName,
                componentIds,
                componentReferences);
        }
    }
}
