package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerScoreConfiguration extends CampaignControllerTriggerConfiguration {
    private static final String SCORE_RESULT = "score_result";
    private static final String CAUSE_EVENT_NAME = "cause_event_name";
    private static final String CHANNEL = "channel";

    private final String scoreResult;
    private final String causeEventName;
    private final String channel;

    public CampaignControllerTriggerScoreConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(SCORE_RESULT) String scoreResult,
        @JsonProperty(CAUSE_EVENT_NAME) String causeEventName,
        @JsonProperty(CHANNEL) String channel,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.SCORE, triggerPhase, name, description, enabled, negated,
            componentReferences);
        this.scoreResult = scoreResult;
        this.causeEventName = causeEventName;
        this.channel = channel;
    }

    @JsonProperty(CAUSE_EVENT_NAME)
    public String getCauseEventName() {
        return causeEventName;
    }

    @JsonProperty(SCORE_RESULT)
    public String getScoreResult() {
        return scoreResult;
    }

    @JsonProperty(CHANNEL)
    public String getChannel() {
        return channel;
    }
}
