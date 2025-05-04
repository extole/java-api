package com.extole.client.rest.campaign.incentive.reward.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardRuleRestException extends ExtoleRestException {

    public static final ErrorCode<RewardRuleRestException> REWARD_RULE_NOT_FOUND = new ErrorCode<>(
        "reward_rule_not_found", 403, "Reward rule not found", "campaign_id", "reward_rule_id");

    public RewardRuleRestException(String uniqueId, ErrorCode<RewardRuleRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
