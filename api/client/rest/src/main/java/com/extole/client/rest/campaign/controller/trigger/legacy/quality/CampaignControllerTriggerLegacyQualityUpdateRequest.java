package com.extole.client.rest.campaign.controller.trigger.legacy.quality;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerLegacyQualityUpdateRequest extends CampaignControllerTriggerRequest {

    private static final String ACTION_TYPE = "action_type";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> actionType;

    public CampaignControllerTriggerLegacyQualityUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(ACTION_TYPE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerActionType> actionType,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.actionType = actionType;
    }

    @Nullable
    @JsonProperty(ACTION_TYPE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> getActionType() {
        return actionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> actionType;

        private Builder() {
        }

        public Builder withActionType(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerActionType> actionType) {
            this.actionType = actionType;
            return this;
        }

        public CampaignControllerTriggerLegacyQualityUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerLegacyQualityUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                actionType,
                componentIds,
                componentReferences);
        }
    }
}
