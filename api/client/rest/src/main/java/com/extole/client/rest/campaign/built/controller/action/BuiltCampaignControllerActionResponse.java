package com.extole.client.rest.campaign.built.controller.action;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.client.rest.campaign.built.controller.action.approve.BuiltCampaignControllerActionApproveResponse;
import com.extole.client.rest.campaign.built.controller.action.cancel.reward.BuiltCampaignControllerActionCancelRewardResponse;
import com.extole.client.rest.campaign.built.controller.action.create.membership.BuiltCampaignControllerActionCreateMembershipResponse;
import com.extole.client.rest.campaign.built.controller.action.creative.BuiltCampaignControllerActionCreativeResponse;
import com.extole.client.rest.campaign.built.controller.action.data.intelligence.BuiltCampaignControllerActionDataIntelligenceResponse;
import com.extole.client.rest.campaign.built.controller.action.decline.BuiltCampaignControllerActionDeclineResponse;
import com.extole.client.rest.campaign.built.controller.action.earn.reward.BuiltCampaignControllerActionEarnRewardResponse;
import com.extole.client.rest.campaign.built.controller.action.email.BuiltCampaignControllerActionEmailResponse;
import com.extole.client.rest.campaign.built.controller.action.expression.BuiltCampaignControllerActionExpressionResponse;
import com.extole.client.rest.campaign.built.controller.action.fire.as.person.BuiltCampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.built.controller.action.fulfill.reward.BuiltCampaignControllerActionFulfillRewardResponse;
import com.extole.client.rest.campaign.built.controller.action.incentivize.BuiltCampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.built.controller.action.incentivize.status.update.BuiltCampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.campaign.built.controller.action.redeem.reward.BuiltCampaignControllerActionRedeemRewardResponse;
import com.extole.client.rest.campaign.built.controller.action.remove.membership.BuiltCampaignControllerActionRemoveMembershipResponse;
import com.extole.client.rest.campaign.built.controller.action.revoke.reward.BuiltCampaignControllerActionRevokeRewardResponse;
import com.extole.client.rest.campaign.built.controller.action.schedule.BuiltCampaignControllerActionScheduleResponse;
import com.extole.client.rest.campaign.built.controller.action.share.BuiltCampaignControllerActionShareEventResponse;
import com.extole.client.rest.campaign.built.controller.action.signal.BuiltCampaignControllerActionSignalResponse;
import com.extole.client.rest.campaign.built.controller.action.signal.v1.BuiltCampaignControllerActionSignalV1Response;
import com.extole.client.rest.campaign.built.controller.action.step.signal.BuiltCampaignControllerActionStepSignalResponse;
import com.extole.client.rest.campaign.built.controller.action.webhook.BuiltCampaignControllerActionWebhookResponse;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.display.BuiltCampaignControllerActionDisplayResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BuiltCampaignControllerActionResponse.JSON_ACTION_TYPE)
@JsonSubTypes({
    @Type(value = BuiltCampaignControllerActionApproveResponse.class, name = "APPROVE"),
    @Type(value = BuiltCampaignControllerActionCancelRewardResponse.class, name = "CANCEL_REWARD"),
    @Type(value = BuiltCampaignControllerActionCreateMembershipResponse.class, name = "CREATE_MEMBERSHIP"),
    @Type(value = BuiltCampaignControllerActionCreativeResponse.class, name = "CREATIVE"),
    @Type(value = BuiltCampaignControllerActionDataIntelligenceResponse.class, name = "DATA_INTELLIGENCE"),
    @Type(value = BuiltCampaignControllerActionDeclineResponse.class, name = "DECLINE"),
    @Type(value = BuiltCampaignControllerActionDisplayResponse.class, name = "DISPLAY"),
    @Type(value = BuiltCampaignControllerActionEarnRewardResponse.class, name = "EARN_REWARD"),
    @Type(value = BuiltCampaignControllerActionEmailResponse.class, name = "EMAIL"),
    @Type(value = BuiltCampaignControllerActionExpressionResponse.class, name = "EXPRESSION"),
    @Type(value = BuiltCampaignControllerActionFireAsPersonResponse.class, name = "FIRE_AS_PERSON"),
    @Type(value = BuiltCampaignControllerActionFulfillRewardResponse.class, name = "FULFILL_REWARD"),
    @Type(value = BuiltCampaignControllerActionIncentivizeResponse.class, name = "INCENTIVIZE"),
    @Type(value = BuiltCampaignControllerActionIncentivizeStatusUpdateResponse.class,
        name = "INCENTIVIZE_STATUS_UPDATE"),
    @Type(value = BuiltCampaignControllerActionRedeemRewardResponse.class, name = "REDEEM_REWARD"),
    @Type(value = BuiltCampaignControllerActionRemoveMembershipResponse.class, name = "REMOVE_MEMBERSHIP"),
    @Type(value = BuiltCampaignControllerActionRevokeRewardResponse.class, name = "REVOKE_REWARD"),
    @Type(value = BuiltCampaignControllerActionScheduleResponse.class, name = "SCHEDULE"),
    @Type(value = BuiltCampaignControllerActionShareEventResponse.class, name = "SHARE_EVENT"),
    @Type(value = BuiltCampaignControllerActionSignalResponse.class, name = "SIGNAL"),
    @Type(value = BuiltCampaignControllerActionSignalV1Response.class, name = "SIGNAL_V1"),
    @Type(value = BuiltCampaignControllerActionStepSignalResponse.class, name = "STEP_SIGNAL"),
    @Type(value = BuiltCampaignControllerActionWebhookResponse.class, name = "WEBHOOK")

})
public abstract class BuiltCampaignControllerActionResponse extends ComponentElementResponse {
    protected static final String JSON_ACTION_ID = "action_id";
    protected static final String JSON_ACTION_TYPE = "action_type";
    protected static final String JSON_QUALITY = "quality";
    protected static final String JSON_ENABLED = "enabled";

    private final String actionId;
    private final CampaignControllerActionType actionType;
    private final CampaignControllerActionQuality quality;
    private final Boolean enabled;

    public BuiltCampaignControllerActionResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_ACTION_TYPE) CampaignControllerActionType actionType,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.actionId = actionId;
        this.actionType = actionType;
        this.quality = quality;
        this.enabled = enabled;
    }

    @JsonProperty(JSON_ACTION_ID)
    public String getActionId() {
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

    @JsonProperty(JSON_ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
