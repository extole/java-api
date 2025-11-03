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

public class CampaignControllerTriggerScoreCreateRequest extends CampaignControllerTriggerRequest {

    private static final String SCORE_RESULT = "score_result";
    private static final String CAUSE_EVENT_NAME = "cause_event_name";
    private static final String EVENT_CHANNEL = "channel";

    private final CampaignControllerTriggerScoreResult scoreResult;
    private final String causeEventName;
    private final String channel;

    public CampaignControllerTriggerScoreCreateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @Nullable @JsonProperty(SCORE_RESULT) CampaignControllerTriggerScoreResult scoreResult,
        @JsonProperty(CAUSE_EVENT_NAME) String causeEventName,
        @Nullable @JsonProperty(EVENT_CHANNEL) String channel,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        this.scoreResult = scoreResult;
        this.causeEventName = causeEventName;
        this.channel = channel;
    }

    @JsonProperty(CAUSE_EVENT_NAME)
    public String getCauseEventName() {
        return causeEventName;
    }

    @Nullable
    @JsonProperty(SCORE_RESULT)
    public CampaignControllerTriggerScoreResult getScoreResult() {
        return scoreResult;
    }

    @Nullable
    @JsonProperty(EVENT_CHANNEL)
    public String getChannel() {
        return channel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private CampaignControllerTriggerScoreResult scoreResult;
        private String causeEventName;
        private String channel;

        private Builder() {
        }

        public Builder withScoreResult(CampaignControllerTriggerScoreResult scoreResult) {
            this.scoreResult = scoreResult;
            return this;
        }

        public Builder withCauseEventName(String causeEventName) {
            this.causeEventName = causeEventName;
            return this;
        }

        public Builder withChannel(String channel) {
            this.channel = channel;
            return this;
        }

        public CampaignControllerTriggerScoreCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerScoreCreateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                scoreResult,
                causeEventName,
                channel,
                componentIds,
                componentReferences);
        }
    }
}
