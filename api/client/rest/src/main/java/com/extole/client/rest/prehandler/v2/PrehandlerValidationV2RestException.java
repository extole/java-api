package com.extole.client.rest.prehandler.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO to be removed in ENG-13399
public class PrehandlerValidationV2RestException extends ExtoleRestException {

    public static final String JSON_ACTION = "action";
    public static final String JSON_ACTION_TYPE = "action_type";
    public static final String JSON_CONDITION_TYPE = "condition_type";
    public static final String JSON_CONDITION = "condition";
    public static final String JSON_ERRORS = "errors";

    public static final ErrorCode<PrehandlerValidationV2RestException> CONDITION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_condition_length_out_of_range", 400,
            "Condition length must be between 1 and 1024 characters", JSON_CONDITION);

    public static final ErrorCode<PrehandlerValidationV2RestException> ACTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_action_length_out_of_range", 400,
            "Action length must be between 1 and 1024 characters", JSON_ACTION);

    public static final ErrorCode<PrehandlerValidationV2RestException> MISSING_ACTION =
        new ErrorCode<>("missing_action", 400, "It's not allowed to have null or missing action");

    public static final ErrorCode<PrehandlerValidationV2RestException> MISSING_ACTION_TYPE =
        new ErrorCode<>("missing_action_type", 400, "It's not allowed to have null or missing action type");

    public static final ErrorCode<PrehandlerValidationV2RestException> MISSING_CONDITION =
        new ErrorCode<>("missing_condition", 400, "It's not allowed to have null or missing condition");

    public static final ErrorCode<PrehandlerValidationV2RestException> MISSING_CONDITION_TYPE =
        new ErrorCode<>("missing_condition_type", 400, "It's not allowed to have null or missing condition type");

    public static final ErrorCode<PrehandlerValidationV2RestException> INVALID_CONDITION =
        new ErrorCode<>("invalid_condition", 400, "Invalid condition", JSON_CONDITION, JSON_CONDITION_TYPE,
            JSON_ERRORS);

    public static final ErrorCode<PrehandlerValidationV2RestException> INVALID_ACTION =
        new ErrorCode<>("invalid_action", 400, "Invalid action", JSON_ACTION, JSON_ACTION_TYPE, JSON_ERRORS);

    public static final ErrorCode<PrehandlerValidationV2RestException> UNKNOWN_ACTION_TYPE =
        new ErrorCode<>("unknown_action_type", 400, "Unknown action type", JSON_ACTION_TYPE);

    public static final ErrorCode<PrehandlerValidationV2RestException> UNKNOWN_CONDITION_TYPE =
        new ErrorCode<>("unknown_condition_type", 400, "Unknown condition type", JSON_CONDITION_TYPE);

    public static final ErrorCode<PrehandlerValidationV2RestException> EVENT_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_default_value_event_name_out_of_range", 400,
            "Event name length must be between 1 and 255 characters", "event_name");

    public static final ErrorCode<PrehandlerValidationV2RestException> EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("prehandler_default_value_event_name_contains_illegal_character", 400,
            "Event name can only contain alphanumeric, dash and underscore characters", "event_name");

    public static final ErrorCode<PrehandlerValidationV2RestException> EVENT_NAME_MISSING =
        new ErrorCode<>("prehandler_default_value_event_name_missing", 400, "Event name is missing");

    public static final ErrorCode<PrehandlerValidationV2RestException> PARAMETER_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_default_value_parameter_name_out_of_range", 400,
            "Parameter name length must be between 1 and 255 characters", "parameter_name");

    public static final ErrorCode<PrehandlerValidationV2RestException> PARAMETER_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("prehandler_default_value_parameter_name_contains_illegal_character", 400,
            "Parameter name can only contain alphanumeric, dash and underscore characters", "parameter_name");

    public static final ErrorCode<PrehandlerValidationV2RestException> PARAMETER_NAME_MISSING =
        new ErrorCode<>("prehandler_default_value_parameter_name_missing", 400, "Parameter name is missing");

    public static final ErrorCode<PrehandlerValidationV2RestException> PARAMETER_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_default_value_parameter_value_out_of_range", 400,
            "Parameter value length must be between 1 and 255 characters", "parameter_value");

    public static final ErrorCode<PrehandlerValidationV2RestException> PARAMETER_VALUE_MISSING =
        new ErrorCode<>("prehandler_default_value_parameter_value_missing", 400, "Parameter value is missing");

    public PrehandlerValidationV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
