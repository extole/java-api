package com.extole.client.rest.impl.campaign.built;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.CampaignLockType;
import com.extole.client.rest.campaign.CampaignState;
import com.extole.client.rest.campaign.built.BuiltCampaignResponse;
import com.extole.client.rest.campaign.built.component.BuiltCampaignComponentResponse;
import com.extole.client.rest.campaign.built.controller.BuiltCampaignStepResponse;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepResponse;
import com.extole.client.rest.campaign.configuration.CampaignType;
import com.extole.client.rest.campaign.incentive.IncentiveResponse;
import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleResponse;
import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleType;
import com.extole.client.rest.campaign.incentive.reward.rule.ExpressionType;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleExpression;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleResponse;
import com.extole.client.rest.campaign.incentive.reward.rule.Rewardee;
import com.extole.client.rest.campaign.incentive.reward.rule.RuleDataMatcherType;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleResponse;
import com.extole.client.rest.campaign.label.CampaignLabelResponse;
import com.extole.client.rest.impl.campaign.built.controller.BuiltCampaignStepResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.BuiltCampaignStepResponseMapperRepository;
import com.extole.client.rest.impl.campaign.built.flow.step.BuiltCampaignFlowStepRestMapper;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.label.CampaignLabelRestMapper;
import com.extole.model.entity.QualityRule;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltRewardRule;
import com.extole.model.entity.campaign.built.BuiltTransitionRule;

@Component
public class BuiltCampaignRestMapper {

    private final CampaignComponentRestMapper componentRestMapper;
    private final BuiltCampaignStepResponseMapperRepository builtStepResponseMapperRepository;
    private final BuiltCampaignFlowStepRestMapper builtFlowStepRestMapper;
    private final CampaignLabelRestMapper labelRestMapper;

    @Autowired
    public BuiltCampaignRestMapper(
        CampaignComponentRestMapper componentRestMapper,
        BuiltCampaignStepResponseMapperRepository builtStepResponseMapperRepository,
        BuiltCampaignFlowStepRestMapper builtFlowStepRestMapper,
        CampaignLabelRestMapper labelRestMapper) {
        this.componentRestMapper = componentRestMapper;
        this.builtStepResponseMapperRepository = builtStepResponseMapperRepository;
        this.builtFlowStepRestMapper = builtFlowStepRestMapper;
        this.labelRestMapper = labelRestMapper;
    }

    public BuiltCampaignResponse toBuiltCampaignResponse(BuiltCampaign campaign, ZoneId timeZone) {
        List<BuiltCampaignComponentResponse> components = campaign.getComponents().stream()
            .map(component -> componentRestMapper.toBuiltComponentResponse(component, timeZone,
                campaign))
            .collect(Collectors.toList());

        List<BuiltCampaignStepResponse> steps = campaign.getSteps().stream()
            .map(step -> {
                BuiltCampaignStepResponseMapper mapper = builtStepResponseMapperRepository.getMapper(step.getType());
                return mapper.toResponse(step, timeZone);
            })
            .sorted(Comparator.comparing(step -> step.getId()))
            .collect(Collectors.toList());

        List<BuiltCampaignFlowStepResponse> flowSteps = campaign.getFlowSteps().stream()
            .map(flowStep -> builtFlowStepRestMapper.toBuiltFlowStepResponse(flowStep))
            .sorted(Comparator.comparing(campaignFlowStepResponse -> campaignFlowStepResponse.getSequence()))
            .collect(Collectors.toList());

        List<CampaignLabelResponse> labels = campaign.getLabels().stream()
            .map(label -> labelRestMapper.toCampaignLabelResponse(label, timeZone))
            .sorted(Comparator.comparing(campaignLabelResponse -> campaignLabelResponse.getName()))
            .collect(Collectors.toList());

        Set<CampaignLockType> campaignLocks = campaign.getLocks().stream()
            .map(lockType -> CampaignLockType.valueOf(lockType.name())).collect(Collectors.toSet());

        IncentiveResponse incentiveResponse = toIncentiveResponse(campaign);

        return new BuiltCampaignResponse(
            campaign.getId().getValue(),
            campaign.getName(),
            campaign.getDescription(),
            incentiveResponse.getId(),
            campaign.getUpdatedDate().atZone(timeZone),
            campaign.getLastPublishedDate().map(date -> date.atZone(timeZone)),
            campaign.getStartDate().map(date -> date.atZone(timeZone)),
            campaign.getStopDate().map(date -> date.atZone(timeZone)),
            campaign.getPausedAt().map(date -> date.atZone(timeZone)),
            campaign.getEndedAt().map(date -> date.atZone(timeZone)),
            !campaign.isDraft(),
            CampaignState.valueOf(campaign.getState().toString()),
            components,
            steps,
            labels,
            campaign.getProgramLabel().getName(),
            incentiveResponse,
            campaign.getVersion(),
            campaign.getParentVersion(),
            campaign.getProgramType(),
            campaign.getThemeName().orElse(null),
            flowSteps,
            campaignLocks,
            campaign.getTags(),
            campaign.getVariantSelector(),
            campaign.getVariants(),
            CampaignType.valueOf(campaign.getCampaignType().name()));
    }

    public IncentiveResponse toIncentiveResponse(BuiltCampaign campaign) {
        List<QualityRuleResponse> qualityRules = campaign.getQualityRules().stream()
            .map(this::toQualityRuleResponse)
            .sorted(Comparator.comparing(QualityRuleResponse::getId))
            .collect(Collectors.toList());

        List<RewardRuleResponse> rewardRules = campaign.getRewardRules().stream()
            .map(this::toRewardRuleResponse)
            .sorted(Comparator.comparing(RewardRuleResponse::getId))
            .collect(Collectors.toList());

        List<TransitionRuleResponse> transitionRules = campaign.getTransitionRules().stream()
            .map(this::toTransitionRuleResponse)
            .sorted(Comparator.comparing(TransitionRuleResponse::getTransitionRuleId))
            .collect(Collectors.toList());

        return new IncentiveResponse(campaign.getIncentiveId().getValue(), qualityRules, rewardRules, transitionRules);
    }

    private QualityRuleResponse toQualityRuleResponse(QualityRule qualityRule) {
        Set<RuleActionType> restRuleActionTypes = new TreeSet<>();
        for (com.extole.model.entity.campaign.RuleActionType actionType : qualityRule.getActionTypes()) {
            restRuleActionTypes.add(RuleActionType.valueOf(actionType.name()));
        }

        return new QualityRuleResponse(
            qualityRule.getId().getValue(),
            Boolean.valueOf(qualityRule.getEnabled()),
            QualityRuleType.valueOf(qualityRule.getRuleType().name()),
            restRuleActionTypes,
            qualityRule.getProperties());
    }

    private RewardRuleResponse toRewardRuleResponse(BuiltRewardRule rewardRule) {
        RuleDataMatcherType dataAttributeType = null;
        if (rewardRule.getDataAttributeMatcherType() != null) {
            dataAttributeType = RuleDataMatcherType.valueOf(rewardRule.getDataAttributeMatcherType().name());
        }

        return new RewardRuleResponse(
            rewardRule.getId().getValue(),
            Rewardee.valueOf(rewardRule.getRewardee().name()),
            rewardRule.getRewardSupplierId().getValue(),
            rewardRule.getReferralsPerReward(),
            rewardRule.getRewardCountLimit(),
            rewardRule.getRewardCountSinceMonth(),
            rewardRule.getRewardCountSinceDays(),
            rewardRule.getRewardValueLimit(),
            rewardRule.getRewardValueSinceMonth(),
            rewardRule.getRewardValueSinceDays(),
            Boolean.valueOf(rewardRule.isUniqueFriendRequired()),
            Boolean.valueOf(rewardRule.isReferralLoopAllowed()),
            rewardRule.getRewardSlots(),
            rewardRule.getMinCartValue(),
            RuleActionType.valueOf(rewardRule.getRuleActionType().name()),
            Boolean.valueOf(rewardRule.isEmailRequired()),
            rewardRule.getDataAttributeName(),
            rewardRule.getDataAttributeValue(),
            dataAttributeType,
            rewardRule.getExpression().map(rule -> new RewardRuleExpression(
                rule.getValue(), ExpressionType.valueOf(rule.getType().name()))).orElse(null),
            rewardRule.getRewardEveryXFriendActions(),
            Boolean.valueOf(rewardRule.isRewardCountingBasedOnPartnerUserId()));
    }

    private TransitionRuleResponse toTransitionRuleResponse(BuiltTransitionRule transitionRule) {
        return new TransitionRuleResponse(
            transitionRule.getId().getValue(),
            RuleActionType.valueOf(transitionRule.getActionType().name()),
            Boolean.valueOf(transitionRule.getApproveLowQuality()),
            Boolean.valueOf(transitionRule.getApproveHighQuality()),
            Long.valueOf(transitionRule.getTransitionPeriod().toMillis()));
    }

}
