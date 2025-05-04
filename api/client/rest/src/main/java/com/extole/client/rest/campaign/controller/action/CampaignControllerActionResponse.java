package com.extole.client.rest.campaign.controller.action;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.approve.CampaignControllerActionApproveResponse;
import com.extole.client.rest.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardResponse;
import com.extole.client.rest.campaign.controller.action.create.membership.CampaignControllerActionCreateMembershipResponse;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeResponse;
import com.extole.client.rest.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceResponse;
import com.extole.client.rest.campaign.controller.action.decline.CampaignControllerActionDeclineResponse;
import com.extole.client.rest.campaign.controller.action.display.CampaignControllerActionDisplayResponse;
import com.extole.client.rest.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardResponse;
import com.extole.client.rest.campaign.controller.action.email.CampaignControllerActionEmailResponse;
import com.extole.client.rest.campaign.controller.action.expression.CampaignControllerActionExpressionResponse;
import com.extole.client.rest.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonResponse;
import com.extole.client.rest.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardResponse;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardResponse;
import com.extole.client.rest.campaign.controller.action.remove.membership.CampaignControllerActionRemoveMembershipResponse;
import com.extole.client.rest.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardResponse;
import com.extole.client.rest.campaign.controller.action.schedule.CampaignControllerActionScheduleResponse;
import com.extole.client.rest.campaign.controller.action.share.CampaignControllerActionShareEventResponse;
import com.extole.client.rest.campaign.controller.action.signal.CampaignControllerActionSignalResponse;
import com.extole.client.rest.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1Response;
import com.extole.client.rest.campaign.controller.action.step.signal.CampaignControllerActionStepSignalResponse;
import com.extole.client.rest.campaign.controller.action.webhook.CampaignControllerActionWebhookResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignControllerActionResponse.JSON_ACTION_TYPE)
@JsonSubTypes({
    @Type(value = CampaignControllerActionApproveResponse.class, name = "APPROVE"),
    @Type(value = CampaignControllerActionCancelRewardResponse.class, name = "CANCEL_REWARD"),
    @Type(value = CampaignControllerActionCreateMembershipResponse.class, name = "CREATE_MEMBERSHIP"),
    @Type(value = CampaignControllerActionCreativeResponse.class, name = "CREATIVE"),
    @Type(value = CampaignControllerActionDataIntelligenceResponse.class, name = "DATA_INTELLIGENCE"),
    @Type(value = CampaignControllerActionDeclineResponse.class, name = "DECLINE"),
    @Type(value = CampaignControllerActionDisplayResponse.class, name = "DISPLAY"),
    @Type(value = CampaignControllerActionEarnRewardResponse.class, name = "EARN_REWARD"),
    @Type(value = CampaignControllerActionEmailResponse.class, name = "EMAIL"),
    @Type(value = CampaignControllerActionExpressionResponse.class, name = "EXPRESSION"),
    @Type(value = CampaignControllerActionFireAsPersonResponse.class, name = "FIRE_AS_PERSON"),
    @Type(value = CampaignControllerActionFulfillRewardResponse.class, name = "FULFILL_REWARD"),
    @Type(value = CampaignControllerActionIncentivizeResponse.class, name = "INCENTIVIZE"),
    @Type(value = CampaignControllerActionIncentivizeStatusUpdateResponse.class, name = "INCENTIVIZE_STATUS_UPDATE"),
    @Type(value = CampaignControllerActionRedeemRewardResponse.class, name = "REDEEM_REWARD"),
    @Type(value = CampaignControllerActionRemoveMembershipResponse.class, name = "REMOVE_MEMBERSHIP"),
    @Type(value = CampaignControllerActionRevokeRewardResponse.class, name = "REVOKE_REWARD"),
    @Type(value = CampaignControllerActionScheduleResponse.class, name = "SCHEDULE"),
    @Type(value = CampaignControllerActionShareEventResponse.class, name = "SHARE_EVENT"),
    @Type(value = CampaignControllerActionSignalResponse.class, name = "SIGNAL"),
    @Type(value = CampaignControllerActionSignalV1Response.class, name = "SIGNAL_V1"),
    @Type(value = CampaignControllerActionStepSignalResponse.class, name = "STEP_SIGNAL"),
    @Type(value = CampaignControllerActionWebhookResponse.class, name = "WEBHOOK")
})
public abstract class CampaignControllerActionResponse extends ComponentElementResponse {
    protected static final String JSON_ACTION_ID = "action_id";
    protected static final String JSON_ACTION_TYPE = "action_type";
    protected static final String JSON_QUALITY = "quality";
    protected static final String JSON_ENABLED = "enabled";

    private final String actionId;
    private final CampaignControllerActionType actionType;
    private final CampaignControllerActionQuality quality;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled;

    public CampaignControllerActionResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_ACTION_TYPE) CampaignControllerActionType actionType,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
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
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
