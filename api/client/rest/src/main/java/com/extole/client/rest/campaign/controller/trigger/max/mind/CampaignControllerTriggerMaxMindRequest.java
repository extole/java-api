package com.extole.client.rest.campaign.controller.trigger.max.mind;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerTriggerMaxMindRequest extends ComponentElementRequest {

    private static final String TRIGGER_PHASE = "trigger_phase";
    private static final String TRIGGER_NAME = "trigger_name";
    private static final String TRIGGER_DESCRIPTION = "trigger_description";
    private static final String ENABLED = "enabled";
    private static final String NEGATED = "negated";
    private static final String DEFAULT_QUALITY_SCORE = "default_quality_score";
    private static final String RISK_THRESHOLD = "risk_threshold";
    private static final String IP_THRESHOLD = "ip_threshold";
    private static final String ALLOW_HIGH_RISK_EMAIL = "allow_high_risk_email";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        CampaignControllerTriggerPhase>> triggerPhase;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> defaultQualityScore;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> riskThreshold;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> ipThreshold;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail;

    public CampaignControllerTriggerMaxMindRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>>> description,
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
        super(componentReferences, componentIds);
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
        this.defaultQualityScore = defaultQualityScore;
        this.riskThreshold = riskThreshold;
        this.ipThreshold = ipThreshold;
        this.allowHighRiskEmail = allowHighRiskEmail;
    }

    @JsonProperty(TRIGGER_PHASE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>>
        getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(TRIGGER_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getNegated() {
        return negated;
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

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase>> triggerPhase = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, QualityScore>> defaultQualityScore =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> riskThreshold = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Long>> ipThreshold = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> allowHighRiskEmail =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withTriggerPhase(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase) {
            this.triggerPhase = Omissible.of(triggerPhase);
            return this;
        }

        public Builder withTriggerPhase(CampaignControllerTriggerPhase triggerPhase) {
            this.triggerPhase = Omissible.of(Provided.of(triggerPhase));
            return this;
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

        public Builder withName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withNegated(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated) {
            this.negated = Omissible.of(negated);
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
