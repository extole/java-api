package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BuiltCampaignControllerTriggerResponse.TRIGGER_TYPE)
@JsonSubTypes({
    @Type(value = BuiltCampaignControllerTriggerShareResponse.class, name = "SHARE"),
    @Type(value = BuiltCampaignControllerTriggerEventResponse.class, name = "EVENT"),
    @Type(value = BuiltCampaignControllerTriggerScoreResponse.class, name = "SCORE"),
    @Type(value = BuiltCampaignControllerTriggerZoneStateResponse.class, name = "ZONE_STATE"),
    @Type(value = BuiltCampaignControllerTriggerReferredByEventResponse.class, name = "REFERRED_BY_EVENT"),
    @Type(value = BuiltCampaignControllerTriggerLegacyQualityResponse.class, name = "LEGACY_QUALITY"),
    @Type(value = BuiltCampaignControllerTriggerExpressionResponse.class, name = "EXPRESSION"),
    @Type(value = BuiltCampaignControllerTriggerAccessResponse.class, name = "ACCESS"),
    @Type(value = BuiltCampaignControllerTriggerDataIntelligenceEventResponse.class, name = "DATA_INTELLIGENCE_EVENT"),
    @Type(value = BuiltCampaignControllerTriggerHasPriorStepResponse.class, name = "HAS_PRIOR_STEP"),
    @Type(value = BuiltCampaignControllerTriggerMaxMindResponse.class, name = "MAXMIND"),
    @Type(value = BuiltCampaignControllerTriggerRewardEventResponse.class, name = "REWARD_EVENT"),
    @Type(value = BuiltCampaignControllerTriggerSendRewardEventResponse.class, name = "SEND_REWARD_EVENT"),
    @Type(value = BuiltCampaignControllerTriggerAudienceMembershipEventResponse.class,
        name = "AUDIENCE_MEMBERSHIP_EVENT"),
    @Type(value = BuiltCampaignControllerTriggerAudienceMembershipResponse.class, name = "AUDIENCE_MEMBERSHIP"),
    @Type(value = BuiltCampaignControllerTriggerHasPriorRewardResponse.class, name = "HAS_PRIOR_REWARD"),
    @Type(value = BuiltCampaignControllerTriggerHasIdentityResponse.class, name = "HAS_IDENTITY"),
    @Type(value = BuiltCampaignControllerTriggerClientDomainResponse.class, name = "CLIENT_DOMAIN"),
    @Type(value = BuiltCampaignControllerTriggerLegacyLabelTargetingResponse.class, name = "LEGACY_LABEL_TARGETING")
})
public abstract class BuiltCampaignControllerTriggerResponse extends ComponentElementResponse {

    protected static final String TRIGGER_ID = "trigger_id";
    protected static final String TRIGGER_TYPE = "trigger_type";
    protected static final String TRIGGER_PHASE = "trigger_phase";
    protected static final String TRIGGER_NAME = "trigger_name";
    protected static final String TRIGGER_DESCRIPTION = "trigger_description";
    protected static final String ENABLED = "enabled";
    protected static final String NEGATED = "negated";

    private final String triggerId;
    private final CampaignControllerTriggerType triggerType;
    private final CampaignControllerTriggerPhase triggerPhase;
    private final String name;
    private final Optional<String> description;
    private final Boolean enabled;
    private final Boolean negated;

    public BuiltCampaignControllerTriggerResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_TYPE) CampaignControllerTriggerType triggerType,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.triggerId = triggerId;
        this.triggerType = triggerType;
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
    }

    @JsonProperty(TRIGGER_ID)
    public String getTriggerId() {
        return triggerId;
    }

    @JsonProperty(TRIGGER_TYPE)
    public CampaignControllerTriggerType getTriggerType() {
        return triggerType;
    }

    @JsonProperty(TRIGGER_PHASE)
    public CampaignControllerTriggerPhase getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public Optional<String> getDescription() {
        return description;
    }

    @JsonProperty(ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public Boolean getNegated() {
        return negated;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }

}
