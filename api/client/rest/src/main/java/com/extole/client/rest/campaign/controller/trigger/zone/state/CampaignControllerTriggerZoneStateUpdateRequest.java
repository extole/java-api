package com.extole.client.rest.campaign.controller.trigger.zone.state;

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
import com.extole.id.Id;

public class CampaignControllerTriggerZoneStateUpdateRequest extends CampaignControllerTriggerRequest {

    private static final String ZONE_NAME = "zone_name";
    private static final String STEP_NAME = "step_name";
    private static final String INVERT_MAPPING_STATE = "invert_mapping_state";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> zoneName;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> stepName;
    private final Omissible<Boolean> invertMappingState;

    public CampaignControllerTriggerZoneStateUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(ZONE_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> zoneName,
        @JsonProperty(STEP_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> stepName,
        @JsonProperty(INVERT_MAPPING_STATE) Omissible<Boolean> invertMappingState,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.zoneName = zoneName;
        this.stepName = stepName;
        this.invertMappingState = invertMappingState;
    }

    @JsonProperty(ZONE_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getZoneName() {
        return zoneName;
    }

    @JsonProperty(STEP_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getStepName() {
        return stepName;
    }

    @JsonProperty(INVERT_MAPPING_STATE)
    public Omissible<Boolean> isInvertMappingState() {
        return invertMappingState;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> zoneName =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> stepName =
            Omissible.omitted();
        private Omissible<Boolean> invertMappingState = Omissible.omitted();

        private Builder() {
        }

        public Builder
            withZoneName(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> zoneName) {
            this.zoneName = Omissible.of(zoneName);
            return this;
        }

        public Builder
            withStepName(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> stepName) {
            this.stepName = Omissible.of(stepName);
            return this;
        }

        public Builder withInvertMappingState(Boolean invertMappingState) {
            this.invertMappingState = Omissible.of(invertMappingState);
            return this;
        }

        public CampaignControllerTriggerZoneStateUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerZoneStateUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                zoneName,
                stepName,
                invertMappingState,
                componentIds,
                componentReferences);
        }
    }
}
