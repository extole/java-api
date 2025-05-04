package com.extole.client.rest.creative;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CreativeVariableRestException extends ExtoleRestException {

    public static final ErrorCode<CreativeVariableRestException> INVALID_ZONE_STATE =
        new ErrorCode<>("invalid_zone_state", 400, "Invalid Zone State", "allowed_values", "current_value");

    public static final ErrorCode<CreativeVariableRestException> INVALID_CREATIVE_ID =
        new ErrorCode<>("invalid_creative_id", 400, "Invalid Creative", "creative_id");

    public static final ErrorCode<CreativeVariableRestException> INVALID_VARIABLE_NAME =
        new ErrorCode<>("invalid_variable_name", 400, "Invalid Variable Name", "variable_name");

    public static final ErrorCode<CreativeVariableRestException> INVALID_VARIABLE_VALUE_FORMAT = new ErrorCode<>(
        "invalid_variable_value_format", 400, "Invalid variable value format", "variable_name", "variable_value");

    public static final ErrorCode<CreativeVariableRestException> INVALID_VARIABLE_TYPE_IMAGE = new ErrorCode<>(
        "invalid_variable_type_image", 400, "Cannot upload images for variables not of type IMAGE", "variable_type");

    public static final ErrorCode<CreativeVariableRestException> CREATIVE_VARIABLE_UNSUPPORTED = new ErrorCode<>(
        "creative_variable_unsupported", 400,
        "Archive does not support creative variables. It is missing write-variables.js", "creative_id", "client_id");

    public static final ErrorCode<CreativeVariableRestException> CREATIVE_VARIABLE_IMAGE_FILE_PATH_TOO_LONG =
        new ErrorCode<>("creative_variable_image_file_path_too_long", 400,
            "Image file path length is greater than max allowed length", "file_path", "file_path_length",
            "max_allowed_file_path_length");

    public static final ErrorCode<CreativeVariableRestException> CREATIVE_VARIABLE_IMAGE_FILE_SIZE_TOO_BIG =
        new ErrorCode<>("creative_variable_image_file_size_too_big", 400,
            "Image file size is greater than max allowed size", "file_path", "file_size", "max_allowed_file_size");

    public static final ErrorCode<CreativeVariableRestException> CREATIVE_ARCHIVE_SIZE_TOO_BIG =
        new ErrorCode<>("creative_archive_size_too_big", 400,
            "Archive becomes greater than max allowed size", "archive_size", "max_allowed_size");

    public static final ErrorCode<CreativeVariableRestException> JAVASCRIPT_ERROR =
        new ErrorCode<>("javascript_error", 400, "Error with creative's javascript", "creative_id", "output");

    public static final ErrorCode<CreativeVariableRestException> INVALID_LOCALE = new ErrorCode<>(
        "invalid_locale", 400, "Invalid locale", "locale");

    public static final ErrorCode<CreativeVariableRestException> DEFAULT_LOCALE_IS_DISABLED = new ErrorCode<>(
        "default_locale_is_disabled", 400, "Default locale must be in the list of enabled locales",
        "default_locale", "enabled_locales");

    public CreativeVariableRestException(String uniqueId, ErrorCode<CreativeVariableRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
