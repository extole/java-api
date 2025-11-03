package com.extole.client.rest.campaign.controller.trigger.max.mind;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerMaxMindResponse extends CampaignControllerTriggerResponse {

    private static final String DEFAULT_QUALITY_SCORE = "default_quality_score";
    private static final String RISK_THRESHOLD = "risk_threshold";
    private static final String IP_THRESHOLD = "ip_threshold";
    private static final String ALLOW_HIGH_RISK_EMAIL = "allow_high_risk_email";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore> defaultQualityScore;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Long> riskThreshold;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Long> ipThreshold;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> allowHighRiskEmail;

    public CampaignControllerTriggerMaxMindResponse(@JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(DEFAULT_QUALITY_SCORE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            QualityScore> defaultQualityScore,
        @JsonProperty(RISK_THRESHOLD) BuildtimeEvaluatable<ControllerBuildtimeContext, Long> riskThreshold,
        @JsonProperty(IP_THRESHOLD) BuildtimeEvaluatable<ControllerBuildtimeContext, Long> ipThreshold,
        @JsonProperty(ALLOW_HIGH_RISK_EMAIL) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Boolean> allowHighRiskEmail,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.MAXMIND, triggerPhase, name, parentTriggerGroupName, description,
            enabled, negated, componentIds, componentReferences);
        this.defaultQualityScore = defaultQualityScore;
        this.riskThreshold = riskThreshold;
        this.ipThreshold = ipThreshold;
        this.allowHighRiskEmail = allowHighRiskEmail;
    }

    @JsonProperty(DEFAULT_QUALITY_SCORE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore> getDefaultQualityScore() {
        return defaultQualityScore;
    }

    @JsonProperty(RISK_THRESHOLD)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Long> getRiskThreshold() {
        return riskThreshold;
    }

    @JsonProperty(IP_THRESHOLD)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Long> getIpThreshold() {
        return ipThreshold;
    }

    @JsonProperty(ALLOW_HIGH_RISK_EMAIL)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> allowHighRiskEmail() {
        return allowHighRiskEmail;
    }

}
