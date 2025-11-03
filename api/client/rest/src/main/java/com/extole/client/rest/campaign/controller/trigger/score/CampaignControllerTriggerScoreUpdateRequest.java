package com.extole.client.rest.campaign.controller.trigger.score;

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

public class CampaignControllerTriggerScoreUpdateRequest extends CampaignControllerTriggerRequest {

    private static final String SCORE_RESULT = "score_result";
    private static final String CAUSE_EVENT_NAME = "cause_event_name";

    private final String scoreResult;
    private final String causeEventName;

    public CampaignControllerTriggerScoreUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @Nullable @JsonProperty(SCORE_RESULT) String scoreResult,
        @Nullable @JsonProperty(CAUSE_EVENT_NAME) String causeEventName,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.scoreResult = scoreResult;
        this.causeEventName = causeEventName;
    }

    @Nullable
    @JsonProperty(CAUSE_EVENT_NAME)
    public String getCauseEventName() {
        return causeEventName;
    }

    @Nullable
    @JsonProperty(SCORE_RESULT)
    public String getScoreResult() {
        return scoreResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private String scoreResult;
        private String causeEventName;

        private Builder() {
        }

        public Builder withScoreResult(String scoreResult) {
            this.scoreResult = scoreResult;
            return this;
        }

        public Builder withCauseEventName(String causeEventName) {
            this.causeEventName = causeEventName;
            return this;
        }

        public CampaignControllerTriggerScoreUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerScoreUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                scoreResult,
                causeEventName,
                componentIds,
                componentReferences);
        }
    }
}
