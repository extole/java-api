package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignControllerTriggerConfiguration.TRIGGER_TYPE)
@JsonSubTypes({
    @Type(value = CampaignControllerTriggerShareConfiguration.class, name = "SHARE"),
    @Type(value = CampaignControllerTriggerEventConfiguration.class, name = "EVENT"),
    @Type(value = CampaignControllerTriggerScoreConfiguration.class, name = "SCORE"),
    @Type(value = CampaignControllerTriggerZoneStateConfiguration.class, name = "ZONE_STATE"),
    @Type(value = CampaignControllerTriggerReferredByEventConfiguration.class, name = "REFERRED_BY_EVENT"),
    @Type(value = CampaignControllerTriggerLegacyQualityConfiguration.class, name = "LEGACY_QUALITY"),
    @Type(value = CampaignControllerTriggerExpressionConfiguration.class, name = "EXPRESSION"),
    @Type(value = CampaignControllerTriggerAccessConfiguration.class, name = "ACCESS"),
    @Type(value = CampaignControllerTriggerDataIntelligenceEventConfiguration.class, name = "DATA_INTELLIGENCE_EVENT"),
    @Type(value = CampaignControllerTriggerHasPriorStepConfiguration.class, name = "HAS_PRIOR_STEP"),
    @Type(value = CampaignControllerTriggerMaxMindConfiguration.class, name = "MAXMIND"),
    @Type(value = CampaignControllerTriggerRewardEventConfiguration.class, name = "REWARD_EVENT"),
    @Type(value = CampaignControllerTriggerSendRewardEventConfiguration.class, name = "SEND_REWARD_EVENT"),
    @Type(value = CampaignControllerTriggerHasPriorRewardConfiguration.class, name = "HAS_PRIOR_REWARD"),
    @Type(value = CampaignControllerTriggerAudienceMembershipEventConfiguration.class,
        name = "AUDIENCE_MEMBERSHIP_EVENT"),
    @Type(value = CampaignControllerTriggerAudienceMembershipConfiguration.class, name = "AUDIENCE_MEMBERSHIP"),
    @Type(value = CampaignControllerTriggerHasIdentityConfiguration.class, name = "HAS_IDENTITY"),
    @Type(value = CampaignControllerTriggerClientDomainConfiguration.class, name = "CLIENT_DOMAIN"),
    @Type(value = CampaignControllerTriggerLegacyLabelTargetingConfiguration.class, name = "LEGACY_LABEL_TARGETING")
})
public abstract class CampaignControllerTriggerConfiguration {

    protected static final String TRIGGER_ID = "trigger_id";
    protected static final String TRIGGER_TYPE = "trigger_type";
    protected static final String TRIGGER_PHASE = "trigger_phase";
    protected static final String TRIGGER_NAME = "trigger_name";
    protected static final String TRIGGER_DESCRIPTION = "trigger_description";
    protected static final String ENABLED = "enabled";
    protected static final String NEGATED = "negated";
    protected static final String COMPONENT_REFERENCES = "component_references";

    private final Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId;
    private final CampaignControllerTriggerType triggerType;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;

    public CampaignControllerTriggerConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_TYPE) CampaignControllerTriggerType triggerType,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        this.triggerId = triggerId;
        this.triggerType = triggerType;
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
        this.componentReferences =
            componentReferences != null ? unmodifiableList(componentReferences) : Collections.emptyList();

    }

    @JsonProperty(TRIGGER_ID)
    public Omissible<Id<CampaignControllerTriggerConfiguration>> getTriggerId() {
        return triggerId;
    }

    @JsonProperty(TRIGGER_TYPE)
    public CampaignControllerTriggerType getTriggerType() {
        return triggerType;
    }

    @JsonProperty(TRIGGER_PHASE)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @JsonProperty(TRIGGER_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(ENABLED)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getNegated() {
        return negated;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }

}
