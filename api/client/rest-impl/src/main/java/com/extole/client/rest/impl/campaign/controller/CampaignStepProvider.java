package com.extole.client.rest.impl.campaign.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.step.data.CampaignStepDataRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ActionableCampaignStep;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerActionApprove;
import com.extole.model.entity.campaign.CampaignControllerActionCancelReward;
import com.extole.model.entity.campaign.CampaignControllerActionCreateMembership;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionDataIntelligence;
import com.extole.model.entity.campaign.CampaignControllerActionDecline;
import com.extole.model.entity.campaign.CampaignControllerActionDisplay;
import com.extole.model.entity.campaign.CampaignControllerActionEarnReward;
import com.extole.model.entity.campaign.CampaignControllerActionEmail;
import com.extole.model.entity.campaign.CampaignControllerActionExpression;
import com.extole.model.entity.campaign.CampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.CampaignControllerActionFulfillReward;
import com.extole.model.entity.campaign.CampaignControllerActionIncentivize;
import com.extole.model.entity.campaign.CampaignControllerActionIncentivizeStatusUpdate;
import com.extole.model.entity.campaign.CampaignControllerActionRedeemReward;
import com.extole.model.entity.campaign.CampaignControllerActionRemoveMembership;
import com.extole.model.entity.campaign.CampaignControllerActionRevokeReward;
import com.extole.model.entity.campaign.CampaignControllerActionSchedule;
import com.extole.model.entity.campaign.CampaignControllerActionShareEvent;
import com.extole.model.entity.campaign.CampaignControllerActionSignal;
import com.extole.model.entity.campaign.CampaignControllerActionSignalV1;
import com.extole.model.entity.campaign.CampaignControllerActionStepSignal;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.CampaignControllerActionWebhook;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignControllerTriggerAccess;
import com.extole.model.entity.campaign.CampaignControllerTriggerAudienceMembership;
import com.extole.model.entity.campaign.CampaignControllerTriggerAudienceMembershipEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerClientDomain;
import com.extole.model.entity.campaign.CampaignControllerTriggerDataIntelligenceEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerExpression;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasIdentity;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorReward;
import com.extole.model.entity.campaign.CampaignControllerTriggerHasPriorStep;
import com.extole.model.entity.campaign.CampaignControllerTriggerLegacyLabelTargeting;
import com.extole.model.entity.campaign.CampaignControllerTriggerLegacyQuality;
import com.extole.model.entity.campaign.CampaignControllerTriggerMaxMind;
import com.extole.model.entity.campaign.CampaignControllerTriggerReferredByEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerRewardEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerScore;
import com.extole.model.entity.campaign.CampaignControllerTriggerSendRewardEvent;
import com.extole.model.entity.campaign.CampaignControllerTriggerShare;
import com.extole.model.entity.campaign.CampaignControllerTriggerZoneState;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.built.BuiltActionableCampaignStep;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionDisplay;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionEmail;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionFireAsPerson;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivize;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivizeStatusUpdate;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerEvent;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerExpression;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTriggerLegacyQuality;
import com.extole.model.entity.campaign.built.BuiltCampaignStep;
import com.extole.model.entity.campaign.built.BuiltStepData;

@Component
public class CampaignStepProvider {

    public CampaignStep getStep(Campaign campaign, String stepId) throws CampaignControllerRestException {
        return campaign.getSteps().stream()
            .filter(step -> step.getId().equals(Id.valueOf(stepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", stepId)
                .build());
    }

    public BuiltCampaignStep getBuiltStep(BuiltCampaign campaign, String stepId)
        throws CampaignControllerRestException {
        return campaign.getSteps().stream()
            .filter(step -> step.getId().equals(Id.valueOf(stepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", stepId)
                .build());
    }

    public FrontendController getFrontendController(Campaign campaign, String controllerId)
        throws CampaignControllerRestException {
        return campaign.getFrontendControllers().stream()
            .filter(controller -> controller.getId().equals(Id.valueOf(controllerId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", controllerId)
                .build());
    }

    public CampaignController getController(Campaign campaign, String controllerId)
        throws CampaignControllerRestException {
        return campaign.getControllers().stream()
            .filter(controller -> controller.getId().equals(Id.valueOf(controllerId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", controllerId)
                .build());
    }

    public ActionableCampaignStep getActionableStep(Campaign campaign, String stepId)
        throws CampaignControllerRestException {
        return campaign.getActionableSteps()
            .stream()
            .filter(step -> step.getId().equals(Id.valueOf(stepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", stepId)
                .build());
    }

    public BuiltActionableCampaignStep getBuiltActionableStep(BuiltCampaign campaign, String stepId)
        throws CampaignControllerRestException {
        return campaign.getActionableSteps()
            .stream()
            .filter(step -> step.getId().equals(Id.valueOf(stepId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
                .withErrorCode(CampaignControllerRestException.INVALID_CAMPAIGN_CONTROLLER_ID)
                .addParameter("campaign_id", campaign.getId())
                .addParameter("controller_id", stepId)
                .build());
    }

    public BuiltStepData getBuiltStepData(BuiltCampaign campaign, String stepId, String stepDataId)
        throws CampaignControllerRestException, CampaignStepDataRestException {
        return getBuiltStep(campaign, stepId).getData().stream()
            .filter(stepData -> stepData.getId().equals(Id.valueOf(stepDataId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignStepDataRestException.class)
                .withErrorCode(CampaignStepDataRestException.INVALID_STEP_DATA_ID)
                .addParameter("step_id", stepId)
                .addParameter("step_data_id", stepDataId)
                .build());
    }

    public CampaignControllerTrigger getStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTrigger.class);
    }

    public CampaignControllerTriggerAccess getAccessStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerAccess.class);
    }

    public CampaignControllerTriggerDataIntelligenceEvent getDataIntelligenceEventStepTrigger(Campaign campaign,
        String stepId, String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerDataIntelligenceEvent.class);
    }

    public CampaignControllerTriggerExpression getExpressionStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerExpression.class);
    }

    public CampaignControllerTriggerHasPriorReward getHasPriorRewardStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerHasPriorReward.class);
    }

    public CampaignControllerTriggerHasPriorStep getHasPriorStepStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerHasPriorStep.class);
    }

    public CampaignControllerTriggerEvent getEventStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerEvent.class);
    }

    public CampaignControllerTriggerReferredByEvent getReferredByEventStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerReferredByEvent.class);
    }

    public CampaignControllerTriggerLegacyQuality getLegacyQualityStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerLegacyQuality.class);
    }

    public CampaignControllerTriggerMaxMind getMaxMindStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerMaxMind.class);
    }

    public CampaignControllerTriggerRewardEvent getRewardEventStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerRewardEvent.class);
    }

    public CampaignControllerTriggerSendRewardEvent getSendRewardEventStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerSendRewardEvent.class);
    }

    public CampaignControllerTriggerAudienceMembership getAudienceMembershipStepTrigger(Campaign campaign,
        String stepId, String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerAudienceMembership.class);
    }

    public CampaignControllerTriggerAudienceMembershipEvent getAudienceMembershipEventStepTrigger(Campaign campaign,
        String stepId, String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerAudienceMembershipEvent.class);
    }

    public CampaignControllerTriggerScore getScoreStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerScore.class);
    }

    public CampaignControllerTriggerShare getShareStepTrigger(Campaign campaign, String stepId, String triggerId)
        throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerShare.class);
    }

    public CampaignControllerTriggerZoneState getZoneStateStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerZoneState.class);
    }

    public BuiltCampaignControllerTriggerEvent getEventBuiltStepTrigger(BuiltCampaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getBuiltTrigger(campaign, stepId, triggerId, BuiltCampaignControllerTriggerEvent.class);
    }

    public BuiltCampaignControllerTriggerExpression getExpressionBuiltStepTrigger(BuiltCampaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getBuiltTrigger(campaign, stepId, triggerId, BuiltCampaignControllerTriggerExpression.class);
    }

    public BuiltCampaignControllerTriggerLegacyQuality getLegacyQualityBuiltStepTrigger(BuiltCampaign campaign,
        String stepId, String triggerId) throws CampaignControllerRestException {
        return getBuiltTrigger(campaign, stepId, triggerId, BuiltCampaignControllerTriggerLegacyQuality.class);
    }

    public CampaignControllerTriggerHasIdentity getHasIdentityStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerHasIdentity.class);
    }

    public CampaignControllerTriggerClientDomain getClientDomainStepTrigger(Campaign campaign, String stepId,
        String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerClientDomain.class);
    }

    public CampaignControllerTriggerLegacyLabelTargeting getLegacyLabelTargetingStepTrigger(Campaign campaign,
        String stepId, String triggerId) throws CampaignControllerRestException {
        return getTrigger(campaign, stepId, triggerId, CampaignControllerTriggerLegacyLabelTargeting.class);
    }

    public CampaignControllerAction getControllerAction(Campaign campaign, String controllerId, String actionId)
        throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerAction.class);
    }

    public CampaignControllerActionIncentivize getIncentivizeControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionIncentivize.class);
    }

    public BuiltCampaignControllerActionIncentivize getIncentivizeBuiltControllerAction(BuiltCampaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getBuiltAction(campaign, controllerId, actionId, BuiltCampaignControllerActionIncentivize.class);
    }

    public CampaignControllerActionIncentivizeStatusUpdate getIncentivizeStatusUpdateControllerAction(Campaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionIncentivizeStatusUpdate.class);
    }

    public BuiltCampaignControllerActionIncentivizeStatusUpdate getIncentivizeStatusUpdateBuiltControllerAction(
        BuiltCampaign campaign, String controllerId, String actionId) throws CampaignControllerRestException {
        return getBuiltAction(campaign, controllerId, actionId,
            BuiltCampaignControllerActionIncentivizeStatusUpdate.class);
    }

    public CampaignControllerActionCreateMembership getCreateMembershipControllerAction(Campaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionCreateMembership.class);
    }

    public CampaignControllerActionRemoveMembership getRemoveMembershipControllerAction(Campaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionRemoveMembership.class);
    }

    public CampaignControllerActionEmail getEmailControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionEmail.class);
    }

    public BuiltCampaignControllerActionEmail getEmailBuiltControllerAction(BuiltCampaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getBuiltAction(campaign, controllerId, actionId, BuiltCampaignControllerActionEmail.class);
    }

    public CampaignControllerActionApprove getApproveControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionApprove.class);
    }

    public CampaignControllerActionDecline getDeclineControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionDecline.class);
    }

    public CampaignControllerActionRevokeReward getRevokeRewardControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionRevokeReward.class);
    }

    public CampaignControllerActionRedeemReward getRedeemRewardControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionRedeemReward.class);
    }

    public CampaignControllerActionEarnReward getEarnRewardControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionEarnReward.class);
    }

    public CampaignControllerActionFulfillReward getFulfillRewardControllerAction(Campaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionFulfillReward.class);
    }

    public CampaignControllerActionCancelReward getCancelRewardControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionCancelReward.class);
    }

    public CampaignControllerActionFireAsPerson getFireAsPersonControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionFireAsPerson.class);
    }

    public BuiltCampaignControllerActionFireAsPerson getFireAsPersonBuiltControllerAction(BuiltCampaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getBuiltAction(campaign, controllerId, actionId, BuiltCampaignControllerActionFireAsPerson.class);
    }

    public BuiltCampaignControllerActionDisplay getDisplayBuiltControllerAction(BuiltCampaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getBuiltAction(campaign, controllerId, actionId, BuiltCampaignControllerActionDisplay.class);
    }

    public CampaignControllerActionShareEvent getShareEventControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionShareEvent.class);
    }

    public CampaignControllerActionSignal getSignalControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionSignal.class);
    }

    public CampaignControllerActionSignalV1 getSignalV1ControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionSignalV1.class);
    }

    public CampaignControllerActionStepSignal getStepSignalControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionStepSignal.class);
    }

    public CampaignControllerActionSchedule getScheduleControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionSchedule.class);
    }

    public CampaignControllerActionDataIntelligence getDataIntelligenceControllerAction(Campaign campaign,
        String controllerId, String actionId) throws CampaignControllerRestException {
        return getAction(campaign, controllerId, actionId, CampaignControllerActionDataIntelligence.class);
    }

    public List<CampaignControllerActionDataIntelligence> getDataIntelligenceControllerActions(Campaign campaign,
        String controllerId) throws CampaignControllerRestException {
        return getActionableStep(campaign, controllerId).getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.DATA_INTELLIGENCE)
            .map(action -> (CampaignControllerActionDataIntelligence) action)
            .collect(Collectors.toList());
    }

    public CampaignControllerActionWebhook getWebhookControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getActionableStep(campaign, controllerId).getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.WEBHOOK)
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .map(action -> (CampaignControllerActionWebhook) action)
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(controllerId, actionId));
    }

    public CampaignControllerActionExpression getExpressionControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getActionableStep(campaign, controllerId).getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.EXPRESSION)
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .map(action -> (CampaignControllerActionExpression) action)
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(controllerId, actionId));
    }

    public CampaignControllerActionCreative getCreativeControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getActionableStep(campaign, controllerId).getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.CREATIVE)
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .map(action -> (CampaignControllerActionCreative) action)
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(controllerId, actionId));
    }

    public CampaignControllerActionDisplay getDisplayControllerAction(Campaign campaign, String controllerId,
        String actionId) throws CampaignControllerRestException {
        return getActionableStep(campaign, controllerId).getActions().stream()
            .filter(action -> action.getType() == CampaignControllerActionType.DISPLAY)
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .map(action -> (CampaignControllerActionDisplay) action)
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(controllerId, actionId));
    }

    public StepData getStepData(Campaign campaign, String stepId, String stepDataId)
        throws CampaignControllerRestException, CampaignStepDataRestException {
        return getStep(campaign, stepId).getData().stream()
            .filter(value -> value.getId().equals(Id.valueOf(stepDataId)))
            .findFirst()
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(CampaignStepDataRestException.class)
                .withErrorCode(CampaignStepDataRestException.INVALID_STEP_DATA_ID)
                .addParameter("step_id", stepId)
                .addParameter("step_data_id", stepDataId)
                .build());
    }

    private <T extends CampaignControllerTrigger> T getTrigger(Campaign campaign, String stepId, String triggerId,
        Class<T> triggerClass) throws CampaignControllerRestException {
        return getStep(campaign, stepId).getTriggers().stream()
            .filter(trigger -> trigger.getId().equals(Id.valueOf(triggerId)))
            .filter(trigger -> triggerClass.isInstance(trigger))
            .map(trigger -> triggerClass.cast(trigger))
            .findFirst()
            .orElseThrow(() -> newTriggerNotFoundException(stepId, triggerId));
    }

    private <T extends BuiltCampaignControllerTrigger> T getBuiltTrigger(BuiltCampaign campaign, String stepId,
        String triggerId, Class<T> triggerClass) throws CampaignControllerRestException {
        return getBuiltStep(campaign, stepId).getTriggers().stream()
            .filter(trigger -> trigger.getId().equals(Id.valueOf(triggerId)))
            .filter(trigger -> triggerClass.isInstance(trigger))
            .map(trigger -> triggerClass.cast(trigger))
            .findFirst()
            .orElseThrow(() -> newTriggerNotFoundException(stepId, triggerId));
    }

    private <T extends CampaignControllerAction> T getAction(Campaign campaign, String stepId, String actionId,
        Class<T> actionClass) throws CampaignControllerRestException {
        return getActionableStep(campaign, stepId).getActions().stream()
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .filter(action -> actionClass.isInstance(action))
            .map(action -> actionClass.cast(action))
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(stepId, actionId));
    }

    private <T extends BuiltCampaignControllerAction> T getBuiltAction(BuiltCampaign campaign, String stepId,
        String actionId, Class<T> actionClass) throws CampaignControllerRestException {
        return getBuiltActionableStep(campaign, stepId).getActions().stream()
            .filter(action -> action.getId().equals(Id.valueOf(actionId)))
            .filter(action -> actionClass.isInstance(action))
            .map(action -> actionClass.cast(action))
            .findFirst()
            .orElseThrow(() -> newActionNotFoundException(stepId, actionId));
    }

    private CampaignControllerRestException newActionNotFoundException(String controllerId, String actionId) {
        return RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
            .withErrorCode(CampaignControllerRestException.INVALID_CONTROLLER_ACTION_ID)
            .addParameter("controller_id", controllerId)
            .addParameter("action_id", actionId)
            .build();
    }

    private CampaignControllerRestException newTriggerNotFoundException(String stepId, String triggerId) {
        return RestExceptionBuilder.newBuilder(CampaignControllerRestException.class)
            .withErrorCode(CampaignControllerRestException.INVALID_CONTROLLER_TRIGGER_ID)
            .addParameter("controller_id", stepId)
            .addParameter("trigger_id", triggerId)
            .build();
    }

}
