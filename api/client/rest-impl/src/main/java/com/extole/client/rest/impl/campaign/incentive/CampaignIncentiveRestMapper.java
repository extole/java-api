package com.extole.client.rest.impl.campaign.incentive;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

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
import com.extole.model.entity.QualityRule;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.RewardRule;
import com.extole.model.entity.campaign.TransitionRule;

@Component
public class CampaignIncentiveRestMapper {

    public IncentiveResponse toResponse(Campaign campaign) {
        List<QualityRuleResponse> qualityRuleResponses = campaign.getQualityRules().stream()
            .map(qualityRule -> toQualityRuleResponse(qualityRule)).collect(Collectors.toList());
        List<RewardRuleResponse> rewardRuleResponses = campaign.getRewardRules().stream()
            .map(rewardRule -> toRewardRuleResponse(rewardRule)).collect(Collectors.toList());
        List<TransitionRuleResponse> transitionRuleResponses = campaign.getTransitionRules().stream()
            .map(transitionRule -> toTransitionRuleResponse(transitionRule)).collect(Collectors.toList());
        return new IncentiveResponse(campaign.getIncentiveId().getValue(),
            qualityRuleResponses, rewardRuleResponses, transitionRuleResponses);
    }

    private QualityRuleResponse toQualityRuleResponse(QualityRule qualityRule) {
        Set<RuleActionType> restRuleActionTypes = new HashSet<>();
        for (com.extole.model.entity.campaign.RuleActionType actionType : qualityRule.getActionTypes()) {
            restRuleActionTypes.add(RuleActionType.valueOf(actionType.name()));
        }
        return new QualityRuleResponse(qualityRule.getId().getValue(), qualityRule.getEnabled(),
            QualityRuleType.valueOf(qualityRule.getRuleType().name()),
            restRuleActionTypes, qualityRule.getProperties());
    }

    private RewardRuleResponse toRewardRuleResponse(RewardRule rewardRule) {
        RuleDataMatcherType dataAttributeType = null;
        if (rewardRule.getDataAttributeMatcherType() != null) {
            dataAttributeType = RuleDataMatcherType.valueOf(rewardRule.getDataAttributeMatcherType().name());
        }
        return new RewardRuleResponse(rewardRule.getId().getValue(), Rewardee.valueOf(rewardRule.getRewardee().name()),
            rewardRule.getRewardSupplierId().getValue(), rewardRule.getReferralsPerReward(),
            rewardRule.getRewardCountLimit(), rewardRule.getRewardCountSinceMonth(),
            rewardRule.getRewardCountSinceDays(), rewardRule.getRewardValueLimit(),
            rewardRule.getRewardValueSinceMonth(), rewardRule.getRewardValueSinceDays(),
            Boolean.valueOf(rewardRule.isUniqueFriendRequired()),
            Boolean.valueOf(rewardRule.isReferralLoopAllowed()),
            rewardRule.getRewardSlots(),
            rewardRule.getMinCartValue(),
            RuleActionType.valueOf(rewardRule.getRuleActionType().name()),
            Boolean.valueOf(rewardRule.isEmailRequired()),
            rewardRule.getDataAttributeName(),
            rewardRule.getDataAttributeValue(),
            dataAttributeType, rewardRule.getExpression().map(rule -> new RewardRuleExpression(
                rule.getValue(), ExpressionType.valueOf(rule.getType().name()))).orElse(null),
            rewardRule.getRewardEveryXFriendActions(),
            rewardRule.isRewardCountingBasedOnPartnerUserId());
    }

    private TransitionRuleResponse toTransitionRuleResponse(TransitionRule transitionRule) {
        return new TransitionRuleResponse(transitionRule.getId().getValue(),
            RuleActionType.valueOf(transitionRule.getActionType().name()),
            transitionRule.getApproveLowQuality(),
            transitionRule.getApproveHighQuality(),
            Long.valueOf(transitionRule.getTransitionPeriod().toMillis()));
    }
}
