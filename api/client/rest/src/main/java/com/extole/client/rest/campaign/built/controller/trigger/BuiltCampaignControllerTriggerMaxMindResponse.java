package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.max.mind.QualityScore;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerMaxMindResponse extends BuiltCampaignControllerTriggerResponse {

    private static final String DEFAULT_QUALITY_SCORE = "default_quality_score";
    private static final String RISK_THRESHOLD = "risk_threshold";
    private static final String IP_THRESHOLD = "ip_threshold";
    private static final String ALLOW_HIGH_RISK_EMAIL = "allow_high_risk_email";

    private final QualityScore defaultQualityScore;
    private final Long riskThreshold;
    private final Long ipThreshold;
    private final Boolean allowHighRiskEmail;

    public BuiltCampaignControllerTriggerMaxMindResponse(@JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(DEFAULT_QUALITY_SCORE) QualityScore eventName,
        @JsonProperty(RISK_THRESHOLD) Long riskThreshold,
        @JsonProperty(IP_THRESHOLD) Long ipThreshold,
        @JsonProperty(ALLOW_HIGH_RISK_EMAIL) Boolean allowHighRiskEmail,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.MAXMIND, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
        this.defaultQualityScore = eventName;
        this.riskThreshold = riskThreshold;
        this.ipThreshold = ipThreshold;
        this.allowHighRiskEmail = allowHighRiskEmail;
    }

    @JsonProperty(DEFAULT_QUALITY_SCORE)
    public QualityScore getDefaultQualityScore() {
        return defaultQualityScore;
    }

    @JsonProperty(RISK_THRESHOLD)
    public long getRiskThreshold() {
        return riskThreshold.longValue();
    }

    @JsonProperty(IP_THRESHOLD)
    public long getIpThreshold() {
        return ipThreshold.longValue();
    }

    @JsonProperty(ALLOW_HIGH_RISK_EMAIL)
    public Boolean allowHighRiskEmail() {
        return allowHighRiskEmail;
    }
}
