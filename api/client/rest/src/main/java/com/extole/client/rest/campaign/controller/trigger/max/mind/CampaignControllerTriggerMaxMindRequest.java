package com.extole.client.rest.campaign.controller.trigger.max.mind;

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

public class CampaignControllerTriggerMaxMindRequest extends CampaignControllerTriggerRequest {

    private static final String DEFAULT_QUALITY_SCORE = "default_quality_score";
    private static final String RISK_THRESHOLD = "risk_threshold";
    private static final String IP_THRESHOLD = "ip_threshold";
    private static final String ALLOW_HIGH_RISK_EMAIL = "allow_high_risk_email";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> defaultQualityScore;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> riskThreshold;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> ipThreshold;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail;

    public CampaignControllerTriggerMaxMindRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(DEFAULT_QUALITY_SCORE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> defaultQualityScore,
        @JsonProperty(RISK_THRESHOLD) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> riskThreshold,
        @JsonProperty(IP_THRESHOLD) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> ipThreshold,
        @JsonProperty(ALLOW_HIGH_RISK_EMAIL) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated,
            componentIds, componentReferences);
        this.defaultQualityScore = defaultQualityScore;
        this.riskThreshold = riskThreshold;
        this.ipThreshold = ipThreshold;
        this.allowHighRiskEmail = allowHighRiskEmail;
    }

    @JsonProperty(DEFAULT_QUALITY_SCORE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> getDefaultQualityScore() {
        return defaultQualityScore;
    }

    @JsonProperty(RISK_THRESHOLD)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> getRiskThreshold() {
        return riskThreshold;
    }

    @JsonProperty(IP_THRESHOLD)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> getIpThreshold() {
        return ipThreshold;
    }

    @JsonProperty(ALLOW_HIGH_RISK_EMAIL)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail() {
        return allowHighRiskEmail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> defaultQualityScore =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> riskThreshold = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> ipThreshold = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withDefaultQualityScore(
            BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore> defaultQualityScore) {
            this.defaultQualityScore = Omissible.of(defaultQualityScore);
            return this;
        }

        public Builder withRiskThreshold(BuildtimeEvaluatable<ControllerBuildtimeContext, Long> riskThreshold) {
            this.riskThreshold = Omissible.of(riskThreshold);
            return this;
        }

        public Builder withIpThreshold(BuildtimeEvaluatable<ControllerBuildtimeContext, Long> ipThreshold) {
            this.ipThreshold = Omissible.of(ipThreshold);
            return this;
        }

        public Builder
            withAllowHighRiskScoreEmail(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> allowHighRiskEmail) {
            this.allowHighRiskEmail = Omissible.of(allowHighRiskEmail);
            return this;
        }

        public CampaignControllerTriggerMaxMindRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerMaxMindRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                defaultQualityScore,
                riskThreshold,
                ipThreshold,
                allowHighRiskEmail,
                componentIds,
                componentReferences);
        }

    }
}
