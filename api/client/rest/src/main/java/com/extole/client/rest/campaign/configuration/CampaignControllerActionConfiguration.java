package com.extole.client.rest.campaign.configuration;

import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;

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
    property = CampaignControllerActionConfiguration.JSON_ACTION_TYPE)
@JsonSubTypes({
    @Type(value = CampaignControllerActionCreativeConfiguration.class, name = "CREATIVE"),
    @Type(value = CampaignControllerActionEmailConfiguration.class, name = "EMAIL"),
    @Type(value = CampaignControllerActionScheduleConfiguration.class, name = "SCHEDULE"),
    @Type(value = CampaignControllerActionSignalConfiguration.class, name = "SIGNAL"),
    @Type(value = CampaignControllerActionSignalV1Configuration.class, name = "SIGNAL_V1"),
    @Type(value = CampaignControllerActionIncentivizeConfiguration.class, name = "INCENTIVIZE"),
    @Type(value = CampaignControllerActionShareEventConfiguration.class, name = "SHARE_EVENT"),
    @Type(value = CampaignControllerActionFireAsPersonConfiguration.class, name = "FIRE_AS_PERSON"),
    @Type(value = CampaignControllerActionDataIntelligenceConfiguration.class, name = "DATA_INTELLIGENCE"),
    @Type(value = CampaignControllerActionApproveConfiguration.class, name = "APPROVE"),
    @Type(value = CampaignControllerActionDeclineConfiguration.class, name = "DECLINE"),
    @Type(value = CampaignControllerActionDisplayConfiguration.class, name = "DISPLAY"),
    @Type(value = CampaignControllerActionEarnRewardConfiguration.class, name = "EARN_REWARD"),
    @Type(value = CampaignControllerActionFulfillRewardConfiguration.class, name = "FULFILL_REWARD"),
    @Type(value = CampaignControllerActionCancelRewardConfiguration.class, name = "CANCEL_REWARD"),
    @Type(value = CampaignControllerActionRedeemRewardConfiguration.class, name = "REDEEM_REWARD"),
    @Type(value = CampaignControllerActionRevokeRewardConfiguration.class, name = "REVOKE_REWARD"),
    @Type(value = CampaignControllerActionStepSignalConfiguration.class, name = "STEP_SIGNAL"),
    @Type(value = CampaignControllerActionWebhookConfiguration.class, name = "WEBHOOK"),
    @Type(value = CampaignControllerActionExpressionConfiguration.class, name = "EXPRESSION"),
    @Type(value = CampaignControllerActionIncentivizeStatusUpdateConfiguration.class,
        name = "INCENTIVIZE_STATUS_UPDATE"),
    @Type(value = CampaignControllerActionCreateMembershipConfiguration.class, name = "CREATE_MEMBERSHIP"),
    @Type(value = CampaignControllerActionRemoveMembershipConfiguration.class, name = "REMOVE_MEMBERSHIP")
})
public abstract class CampaignControllerActionConfiguration {
    protected static final String JSON_ACTION_ID = "action_id";
    protected static final String JSON_ACTION_TYPE = "action_type";
    protected static final String JSON_QUALITY = "quality";
    protected static final String JSON_ENABLED = "enabled";
    protected static final String JSON_COMPONENT_REFERENCES = "component_references";

    private final Omissible<Id<CampaignControllerActionConfiguration>> actionId;
    private final CampaignControllerActionType actionType;
    private final CampaignControllerActionQuality quality;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;

    protected CampaignControllerActionConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_ACTION_TYPE) CampaignControllerActionType actionType,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.quality = quality;
        this.enabled = enabled;
        this.componentReferences =
            componentReferences != null ? unmodifiableList(componentReferences) : Collections.emptyList();
    }

    @JsonProperty(JSON_ACTION_ID)
    public Omissible<Id<CampaignControllerActionConfiguration>> getActionId() {
        return actionId;
    }

    @JsonProperty(JSON_ACTION_TYPE)
    public CampaignControllerActionType getActionType() {
        return actionType;
    }

    @JsonProperty(JSON_QUALITY)
    public CampaignControllerActionQuality getQuality() {
        return quality;
    }

    @JsonProperty(JSON_COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
    }

    @JsonProperty(JSON_ENABLED)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
