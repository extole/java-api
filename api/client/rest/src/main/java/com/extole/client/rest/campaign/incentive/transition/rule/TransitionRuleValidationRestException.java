package com.extole.client.rest.campaign.incentive.transition.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TransitionRuleValidationRestException extends ExtoleRestException {

    public static final ErrorCode<TransitionRuleValidationRestException> TRANSITION_PERIOD_MILLISECONDS_INVALID =
        new ErrorCode<>("transition_period_milliseconds_invalid", 403, "Transition period must be a positive long",
            "transition_period_milliseconds");

    public static final ErrorCode<TransitionRuleValidationRestException> ACTION_TYPE_ALREADY_EXISTS =
        new ErrorCode<>("action_type_already_exists", 403,
            "Transition rule action type already exists. Cannot duplicate", "action_type");

    public TransitionRuleValidationRestException(String uniqueId, ErrorCode<TransitionRuleValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
