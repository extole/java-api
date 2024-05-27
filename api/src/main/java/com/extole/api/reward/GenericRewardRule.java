package com.extole.api.reward;

public interface GenericRewardRule {
    boolean evaluate(RewardRuleContext context);
}
