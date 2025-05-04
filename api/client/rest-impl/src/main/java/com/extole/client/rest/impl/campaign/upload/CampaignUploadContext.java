package com.extole.client.rest.impl.campaign.upload;

import java.util.Map;

import com.google.common.io.ByteSource;

import com.extole.client.rest.campaign.configuration.CampaignComponentAssetConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionApproveConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCancelRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreateMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreativeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDataIntelligenceConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDeclineConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionDisplayConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEarnRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEmailConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFireAsPersonConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionFulfillRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeStatusUpdateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRedeemRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRemoveMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionRevokeRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionScheduleConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionShareEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionSignalConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionSignalV1Configuration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionStepSignalConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionWebhookConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAccessConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerAudienceMembershipEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerClientDomainConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerDataIntelligenceEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerExpressionConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasIdentityConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorRewardConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerHasPriorStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerLegacyLabelTargetingConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerLegacyQualityConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerMaxMindConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerReferredByEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerRewardEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerScoreConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerSendRewardEventConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerShareConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerZoneStateConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepAppConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepMetricConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFrontendControllerConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignJourneyEntryConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignLabelConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.configuration.QualityRuleConfiguration;
import com.extole.client.rest.campaign.configuration.RewardRuleConfiguration;
import com.extole.client.rest.campaign.configuration.StepDataConfiguration;
import com.extole.client.rest.campaign.configuration.TransitionRuleConfiguration;
import com.extole.client.rest.impl.campaign.component.asset.UploadedAssetId;
import com.extole.model.service.campaign.component.CampaignComponentBuilder;
import com.extole.model.service.campaign.component.asset.CampaignComponentAssetBuilder;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.controller.action.approve.CampaignControllerActionApproveBuilder;
import com.extole.model.service.campaign.controller.action.cancel.reward.CampaignControllerActionCancelRewardBuilder;
import com.extole.model.service.campaign.controller.action.create.membership.CampaignControllerActionCreateMembershipBuilder;
import com.extole.model.service.campaign.controller.action.creative.CampaignControllerActionCreativeBuilder;
import com.extole.model.service.campaign.controller.action.data.intelligence.CampaignControllerActionDataIntelligenceBuilder;
import com.extole.model.service.campaign.controller.action.decline.CampaignControllerActionDeclineBuilder;
import com.extole.model.service.campaign.controller.action.display.CampaignControllerActionDisplayBuilder;
import com.extole.model.service.campaign.controller.action.earn.reward.CampaignControllerActionEarnRewardBuilder;
import com.extole.model.service.campaign.controller.action.email.CampaignControllerActionEmailBuilder;
import com.extole.model.service.campaign.controller.action.expression.CampaignControllerActionExpressionBuilder;
import com.extole.model.service.campaign.controller.action.fire.as.person.CampaignControllerActionFireAsPersonBuilder;
import com.extole.model.service.campaign.controller.action.fulfill.reward.CampaignControllerActionFulfillRewardBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeBuilder;
import com.extole.model.service.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateBuilder;
import com.extole.model.service.campaign.controller.action.redeem.reward.CampaignControllerActionRedeemRewardBuilder;
import com.extole.model.service.campaign.controller.action.remove.membership.CampaignControllerActionRemoveMembershipBuilder;
import com.extole.model.service.campaign.controller.action.revoke.reward.CampaignControllerActionRevokeRewardBuilder;
import com.extole.model.service.campaign.controller.action.schedule.CampaignControllerActionScheduleBuilder;
import com.extole.model.service.campaign.controller.action.share.CampaignControllerActionShareEventBuilder;
import com.extole.model.service.campaign.controller.action.signal.CampaignControllerActionSignalBuilder;
import com.extole.model.service.campaign.controller.action.signal.v1.CampaignControllerActionSignalV1Builder;
import com.extole.model.service.campaign.controller.action.step.signal.CampaignControllerActionStepSignalBuilder;
import com.extole.model.service.campaign.controller.action.webhook.CampaignControllerActionWebhookBuilder;
import com.extole.model.service.campaign.controller.data.intelligence.event.CampaignControllerTriggerDataIntelligenceEventBuilder;
import com.extole.model.service.campaign.controller.max.mind.CampaignControllerTriggerMaxMindBuilder;
import com.extole.model.service.campaign.controller.trigger.access.CampaignControllerTriggerAccessBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.CampaignControllerTriggerAudienceMembershipBuilder;
import com.extole.model.service.campaign.controller.trigger.audience.membership.event.CampaignControllerTriggerAudienceMembershipEventBuilder;
import com.extole.model.service.campaign.controller.trigger.client.domain.CampaignControllerTriggerClientDomainBuilder;
import com.extole.model.service.campaign.controller.trigger.event.CampaignControllerTriggerEventBuilder;
import com.extole.model.service.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionBuilder;
import com.extole.model.service.campaign.controller.trigger.has.identity.CampaignControllerTriggerHasIdentityBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.reward.CampaignControllerTriggerHasPriorRewardBuilder;
import com.extole.model.service.campaign.controller.trigger.has.prior.step.CampaignControllerTriggerHasPriorStepBuilder;
import com.extole.model.service.campaign.controller.trigger.legacy.label.targeting.CampaignControllerTriggerLegacyLabelTargetingBuilder;
import com.extole.model.service.campaign.controller.trigger.legacy.quality.CampaignControllerTriggerLegacyQualityBuilder;
import com.extole.model.service.campaign.controller.trigger.referred.by.event.CampaignControllerTriggerReferredByEventBuilder;
import com.extole.model.service.campaign.controller.trigger.reward.event.CampaignControllerTriggerRewardEventBuilder;
import com.extole.model.service.campaign.controller.trigger.score.CampaignControllerTriggerScoreBuilder;
import com.extole.model.service.campaign.controller.trigger.send.reward.event.CampaignControllerTriggerSendRewardEventBuilder;
import com.extole.model.service.campaign.controller.trigger.share.CampaignControllerTriggerShareBuilder;
import com.extole.model.service.campaign.controller.trigger.zone.state.CampaignControllerTriggerZoneStateBuilder;
import com.extole.model.service.campaign.flow.step.CampaignFlowStepBuilder;
import com.extole.model.service.campaign.flow.step.app.CampaignFlowStepAppBuilder;
import com.extole.model.service.campaign.flow.step.metric.CampaignFlowStepMetricBuilder;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryBuilder;
import com.extole.model.service.campaign.label.CampaignLabelBuilder;
import com.extole.model.service.campaign.quality.rule.QualityRuleBuilder;
import com.extole.model.service.campaign.reward.rule.RewardRuleBuilder;
import com.extole.model.service.campaign.setting.SettingBuilder;
import com.extole.model.service.campaign.setting.VariableBuilder;
import com.extole.model.service.campaign.step.data.StepDataBuilder;
import com.extole.model.service.campaign.transition.rule.TransitionRuleBuilder;

public interface CampaignUploadContext {

    Map<String, ByteSource> getCreatives();

    Map<UploadedAssetId, ByteSource> getAssets();

    CampaignComponentBuilder get(CampaignComponentConfiguration component);

    SettingBuilder get(CampaignComponentConfiguration component, CampaignComponentSettingConfiguration setting);

    VariableBuilder get(CampaignComponentConfiguration component, CampaignComponentSocketConfiguration socket,
        CampaignComponentVariableConfiguration variable);

    CampaignComponentAssetBuilder get(CampaignComponentConfiguration component,
        CampaignComponentAssetConfiguration asset);

    CampaignControllerBuilder get(CampaignControllerConfiguration controller);

    FrontendControllerBuilder get(CampaignFrontendControllerConfiguration controller);

    CampaignJourneyEntryBuilder get(CampaignJourneyEntryConfiguration controller);

    CampaignFlowStepBuilder get(CampaignFlowStepConfiguration flowStep);

    CampaignFlowStepMetricBuilder get(CampaignFlowStepConfiguration flowStep,
        CampaignFlowStepMetricConfiguration metric);

    CampaignFlowStepAppBuilder get(CampaignFlowStepConfiguration flowStep, CampaignFlowStepAppConfiguration app);

    CampaignLabelBuilder get(CampaignLabelConfiguration label);

    QualityRuleBuilder get(QualityRuleConfiguration qualityRule);

    RewardRuleBuilder get(RewardRuleConfiguration rewardRule);

    TransitionRuleBuilder get(TransitionRuleConfiguration transitionRule);

    CampaignControllerTriggerZoneStateBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerZoneStateConfiguration trigger);

    CampaignControllerTriggerReferredByEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerReferredByEventConfiguration trigger);

    CampaignControllerTriggerExpressionBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerExpressionConfiguration trigger);

    CampaignControllerTriggerHasIdentityBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasIdentityConfiguration trigger);

    CampaignControllerTriggerClientDomainBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerClientDomainConfiguration trigger);

    CampaignControllerTriggerHasPriorStepBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorStepConfiguration trigger);

    CampaignControllerTriggerAudienceMembershipEventBuilder
        get(CampaignStepConfiguration step, CampaignControllerTriggerAudienceMembershipEventConfiguration trigger);

    CampaignControllerTriggerSendRewardEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerSendRewardEventConfiguration trigger);

    CampaignControllerTriggerRewardEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerRewardEventConfiguration trigger);

    CampaignControllerTriggerMaxMindBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerMaxMindConfiguration trigger);

    CampaignControllerTriggerAccessBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerAccessConfiguration trigger);

    CampaignControllerTriggerLegacyLabelTargetingBuilder
        get(CampaignStepConfiguration step, CampaignControllerTriggerLegacyLabelTargetingConfiguration trigger);

    CampaignControllerTriggerShareBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerShareConfiguration trigger);

    CampaignControllerTriggerLegacyQualityBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerLegacyQualityConfiguration trigger);

    CampaignControllerTriggerScoreBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerScoreConfiguration trigger);

    CampaignControllerTriggerHasPriorRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerHasPriorRewardConfiguration trigger);

    CampaignControllerTriggerEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerTriggerEventConfiguration trigger);

    CampaignControllerTriggerDataIntelligenceEventBuilder
        get(CampaignStepConfiguration step, CampaignControllerTriggerDataIntelligenceEventConfiguration trigger);

    CampaignControllerTriggerAudienceMembershipBuilder
        get(CampaignStepConfiguration step, CampaignControllerTriggerAudienceMembershipConfiguration trigger);

    CampaignControllerActionIncentivizeStatusUpdateBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeStatusUpdateConfiguration action);

    CampaignControllerActionFireAsPersonBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionFireAsPersonConfiguration action);

    CampaignControllerActionIncentivizeBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionIncentivizeConfiguration action);

    CampaignControllerActionExpressionBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionExpressionConfiguration action);

    CampaignControllerActionSignalV1Builder get(CampaignStepConfiguration step,
        CampaignControllerActionSignalV1Configuration action);

    CampaignControllerActionFulfillRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionFulfillRewardConfiguration action);

    CampaignControllerActionDataIntelligenceBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDataIntelligenceConfiguration action);

    CampaignControllerActionRevokeRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRevokeRewardConfiguration action);

    CampaignControllerActionScheduleBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionScheduleConfiguration action);

    CampaignControllerActionDeclineBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDeclineConfiguration action);

    CampaignControllerActionDisplayBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionDisplayConfiguration action);

    CampaignControllerActionRemoveMembershipBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRemoveMembershipConfiguration action);

    CampaignControllerActionCreateMembershipBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCreateMembershipConfiguration action);

    CampaignControllerActionShareEventBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionShareEventConfiguration action);

    CampaignControllerActionStepSignalBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionStepSignalConfiguration action);

    CampaignControllerActionRedeemRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionRedeemRewardConfiguration action);

    CampaignControllerActionSignalBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionSignalConfiguration action);

    CampaignControllerActionWebhookBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionWebhookConfiguration action);

    CampaignControllerActionCancelRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCancelRewardConfiguration action);

    CampaignControllerActionCreativeBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionCreativeConfiguration action);

    CampaignControllerActionEarnRewardBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionEarnRewardConfiguration action);

    CampaignControllerActionEmailBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionEmailConfiguration action);

    CampaignControllerActionApproveBuilder get(CampaignStepConfiguration step,
        CampaignControllerActionApproveConfiguration action);

    StepDataBuilder get(CampaignControllerConfiguration controller, StepDataConfiguration data);

    StepDataBuilder get(CampaignFrontendControllerConfiguration controller, StepDataConfiguration data);

    StepDataBuilder get(CampaignJourneyEntryConfiguration journeyEntry, StepDataConfiguration data);

}
