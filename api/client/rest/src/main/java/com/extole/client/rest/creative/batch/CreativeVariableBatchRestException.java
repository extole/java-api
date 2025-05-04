package com.extole.client.rest.creative.batch;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CreativeVariableBatchRestException extends ExtoleRestException {

    public static final ErrorCode<CreativeVariableBatchRestException> INVALID_ZONE_STATE =
        new ErrorCode<>("invalid_zone_state", 400, "Invalid Zone State", "allowed_values", "current_value");

    public static final ErrorCode<CreativeVariableBatchRestException> INVALID_CREATIVE_ID =
        new ErrorCode<>("invalid_creative_id", 400, "Invalid Creative", "creative_id");

    public static final ErrorCode<CreativeVariableBatchRestException> INVALID_VARIABLES_TYPE =
        new ErrorCode<>("invalid_variables_type", 400, "Invalid variables type", "type");

    public static final ErrorCode<CreativeVariableBatchRestException> VARIABLES_NOT_FOUND =
        new ErrorCode<>("variables_not_found", 400, "Variables not found", "variables_names", "scope");

    public static final ErrorCode<CreativeVariableBatchRestException> MISSING_ZONE_NAME =
        new ErrorCode<>("missing_zone_name", 400, "Zone name must be present", "variables_names");

    public static final ErrorCode<CreativeVariableBatchRestException> MISSING_VARIABLE_NAME =
        new ErrorCode<>("missing_variable_name", 400, "Variable name must be present");

    public static final ErrorCode<CreativeVariableBatchRestException> MISSING_CREATIVE_ARCHIVE_ID =
        new ErrorCode<>("missing_creative_archive_id", 400, "Creative archive ID must be present", "variables_names");

    public static final ErrorCode<CreativeVariableBatchRestException> CREATIVE_VARIABLE_UNSUPPORTED = new ErrorCode<>(
        "creative_variable_unsupported", 400,
        "Archive does not support creative variables. It is missing write-variables.js", "creative_id", "client_id");

    public static final ErrorCode<CreativeVariableBatchRestException> CREATIVE_ARCHIVE_SIZE_TOO_BIG =
        new ErrorCode<>("creative_archive_size_too_big", 400,
            "Archive becomes greater than max allowed size", "archive_size", "max_allowed_size");

    public static final ErrorCode<CreativeVariableBatchRestException> JAVASCRIPT_ERROR =
        new ErrorCode<>("javascript_error", 400, "Error with creative's javascript", "creative_id", "output");

    public static final ErrorCode<CreativeVariableBatchRestException> INVALID_LOCALE = new ErrorCode<>(
        "invalid_locale", 400, "Invalid locale", "locale");

    public CreativeVariableBatchRestException(String uniqueId, ErrorCode<CreativeVariableBatchRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
