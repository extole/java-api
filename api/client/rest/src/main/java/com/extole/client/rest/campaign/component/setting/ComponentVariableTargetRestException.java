package com.extole.client.rest.campaign.component.setting;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentVariableTargetRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentVariableTargetRestException> UNRELATED_TARGET_COMPONENT_VARIABLES =
        new ErrorCode<>("unrelated_target_component_variables", 400,
            "Unrelated variables found for target component", "unrelated_variables", "target_component_id");

    public ComponentVariableTargetRestException(String uniqueId,
        ErrorCode<ComponentVariableTargetRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
