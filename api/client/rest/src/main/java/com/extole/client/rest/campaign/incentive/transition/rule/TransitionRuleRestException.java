package com.extole.client.rest.campaign.incentive.transition.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TransitionRuleRestException extends ExtoleRestException {

    public static final ErrorCode<TransitionRuleRestException> TRANSITION_RULE_NOT_FOUND = new ErrorCode<>(
        "transition_rule_not_found", 403, "Transition rule not found", "campaign_id", "transition_rule_id");

    public TransitionRuleRestException(String uniqueId, ErrorCode<TransitionRuleRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
