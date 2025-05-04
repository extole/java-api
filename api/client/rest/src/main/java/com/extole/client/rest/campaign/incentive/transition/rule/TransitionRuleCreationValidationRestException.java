package com.extole.client.rest.campaign.incentive.transition.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TransitionRuleCreationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<TransitionRuleCreationValidationRestException> ACTION_TYPE_REQUIRED =
        new ErrorCode<>("action_type_required", 403, "Transition rule action type cannot be null",
            "action_type");

    public static final ErrorCode<
        TransitionRuleCreationValidationRestException> TRANSITION_PERIOD_MILLISECONDS_REQUIRED =
            new ErrorCode<>("transition_period_milliseconds_required", 403,
                "Transition period milliseconds cannot be null",
                "transition_period_milliseconds");

    public TransitionRuleCreationValidationRestException(String uniqueId,
        ErrorCode<TransitionRuleCreationValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
