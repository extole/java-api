package com.extole.client.rest.prehandler;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildPrehandlerRestException extends ExtoleRestException {
    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_NAME_MISSING =
        new ErrorCode<>("prehandler_name_missing", 400, "Prehandler name is missing");

    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_name_length_out_of_range", 400,
            "Prehandler name length must be between 1 and 255 characters");

    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("prehandler_name_contains_illegal_character", 400,
            "Prehandler name can only contain alphanumeric, dash, dot and underscore characters", "name");

    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_NAME_DUPLICATED = new ErrorCode<>(
        "prehandler_name_duplicated", 400, "Prehandler with this name already exists for the current client", "name");

    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("prehandler_description_length_out_of_range", 400,
            "Prehandler description length must be between 1 and 512 characters");

    public static final ErrorCode<BuildPrehandlerRestException> TAG_INVALID = new ErrorCode<>(
        "prehandler_tag_invalid", 400, "Tag max length is 255", "tag", "tag_max_length");

    public static final ErrorCode<BuildPrehandlerRestException> INVALID_COMPONENT_REFERENCE =
        new ErrorCode<>("invalid_component_reference", 400, "Unknown referenced campaign component",
            "identifier_type", "identifier");

    public static final ErrorCode<BuildPrehandlerRestException> PREHANDLER_BUILD_FAILED =
        new ErrorCode<>("prehandler_build_failed", 400, "Prehandler build failed",
            "prehandler_id", "evaluatable_name", "evaluatable");

    public BuildPrehandlerRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
