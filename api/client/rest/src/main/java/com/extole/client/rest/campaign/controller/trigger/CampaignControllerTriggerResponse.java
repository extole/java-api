package com.extole.client.rest.campaign.controller.trigger;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.access.CampaignControllerTriggerAccessResponse;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipResponse;
import com.extole.client.rest.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventResponse;
import com.extole.client.rest.campaign.controller.trigger.client.domain.CampaignControllerTriggerClientDomainResponse;
import com.extole.client.rest.campaign.controller.trigger.data.intelligence.event.CampaignControllerTriggerDataIntelligenceEventResponse;
import com.extole.client.rest.campaign.controller.trigger.event.CampaignControllerTriggerEventResponse;
import com.extole.client.rest.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionResponse;
import com.extole.client.rest.campaign.controller.trigger.group.CampaignControllerTriggerGroupResponse;
import com.extole.client.rest.campaign.controller.trigger.has.identity.CampaignControllerTriggerHasIdentityResponse;
import com.extole.client.rest.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardResponse;
import com.extole.client.rest.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepResponse;
import com.extole.client.rest.campaign.controller.trigger.legacy.label.targeting.CampaignControllerTriggerLegacyLabelTargetingResponse;
import com.extole.client.rest.campaign.controller.trigger.legacy.quality.CampaignControllerTriggerLegacyQualityResponse;
import com.extole.client.rest.campaign.controller.trigger.max.mind.CampaignControllerTriggerMaxMindResponse;
import com.extole.client.rest.campaign.controller.trigger.referred.by.CampaignControllerTriggerReferredByEventResponse;
import com.extole.client.rest.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventResponse;
import com.extole.client.rest.campaign.controller.trigger.score.CampaignControllerTriggerScoreResponse;
import com.extole.client.rest.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventResponse;
import com.extole.client.rest.campaign.controller.trigger.share.CampaignControllerTriggerShareResponse;
import com.extole.client.rest.campaign.controller.trigger.step.event.CampaignControllerTriggerStepEventResponse;
import com.extole.client.rest.campaign.controller.trigger.targeting.CampaignControllerTriggerTargetingResponse;
import com.extole.client.rest.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignControllerTriggerResponse.TRIGGER_TYPE)
@JsonSubTypes({
    @Type(value = CampaignControllerTriggerShareResponse.class, name = "SHARE"),
    @Type(value = CampaignControllerTriggerEventResponse.class, name = "EVENT"),
    @Type(value = CampaignControllerTriggerScoreResponse.class, name = "SCORE"),
    @Type(value = CampaignControllerTriggerZoneStateResponse.class, name = "ZONE_STATE"),
    @Type(value = CampaignControllerTriggerReferredByEventResponse.class, name = "REFERRED_BY_EVENT"),
    @Type(value = CampaignControllerTriggerLegacyQualityResponse.class, name = "LEGACY_QUALITY"),
    @Type(value = CampaignControllerTriggerExpressionResponse.class, name = "EXPRESSION"),
    @Type(value = CampaignControllerTriggerAccessResponse.class, name = "ACCESS"),
    @Type(value = CampaignControllerTriggerDataIntelligenceEventResponse.class, name = "DATA_INTELLIGENCE_EVENT"),
    @Type(value = CampaignControllerTriggerHasPriorStepResponse.class, name = "HAS_PRIOR_STEP"),
    @Type(value = CampaignControllerTriggerMaxMindResponse.class, name = "MAXMIND"),
    @Type(value = CampaignControllerTriggerRewardEventResponse.class, name = "REWARD_EVENT"),
    @Type(value = CampaignControllerTriggerSendRewardEventResponse.class, name = "SEND_REWARD_EVENT"),
    @Type(value = CampaignControllerTriggerAudienceMembershipEventResponse.class, name = "AUDIENCE_MEMBERSHIP_EVENT"),
    @Type(value = CampaignControllerTriggerAudienceMembershipResponse.class, name = "AUDIENCE_MEMBERSHIP"),
    @Type(value = CampaignControllerTriggerHasPriorRewardResponse.class, name = "HAS_PRIOR_REWARD"),
    @Type(value = CampaignControllerTriggerHasIdentityResponse.class, name = "HAS_IDENTITY"),
    @Type(value = CampaignControllerTriggerClientDomainResponse.class, name = "CLIENT_DOMAIN"),
    @Type(value = CampaignControllerTriggerLegacyLabelTargetingResponse.class, name = "LEGACY_LABEL_TARGETING"),
    @Type(value = CampaignControllerTriggerStepEventResponse.class, name = "STEP_EVENT"),
    @Type(value = CampaignControllerTriggerTargetingResponse.class, name = "TARGETING"),
    @Type(value = CampaignControllerTriggerGroupResponse.class, name = "GROUP")

})
public abstract class CampaignControllerTriggerResponse extends ComponentElementResponse {

    protected static final String TRIGGER_ID = "trigger_id";
    protected static final String TRIGGER_TYPE = "trigger_type";
    protected static final String TRIGGER_PHASE = "trigger_phase";
    protected static final String TRIGGER_NAME = "trigger_name";
    protected static final String PARENT_TRIGGER_GROUP_NAME = "parent_trigger_group_name";
    protected static final String TRIGGER_DESCRIPTION = "trigger_description";
    protected static final String ENABLED = "enabled";
    protected static final String NEGATED = "negated";

    private final String triggerId;
    private final CampaignControllerTriggerType triggerType;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> parentTriggerGroupName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated;

    public CampaignControllerTriggerResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_TYPE) CampaignControllerTriggerType triggerType,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.triggerId = triggerId;
        this.triggerType = triggerType;
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.parentTriggerGroupName = parentTriggerGroupName;
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
    public BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(PARENT_TRIGGER_GROUP_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> getParentTriggerGroupName() {
        return parentTriggerGroupName;
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
