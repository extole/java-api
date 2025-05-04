package com.extole.reporting.rest.fixup.transformation;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ConditionalAliasChangeFixupTransformationValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ConditionalAliasChangeFixupTransformationValidationRestException> CLIENT_ID_MISSING =
        new ErrorCode<>("client_id_missing", 400, "Client id is missing");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> CLIENT_ID_FILTER_LENGTH =
            new ErrorCode<>("client_id_filter_invalid_length", 400,
                "Client id filter length should be between 1 and 256 characters");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> PROGRAM_LABEL_FILTER_LENGTH =
            new ErrorCode<>("program_label_filter_invalid_length", 400,
                "Program label filter length should be between 1 and 256 characters");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> STEP_NAME_FILTER_LENGTH =
            new ErrorCode<>("step_name_filter_invalid_length", 400,
                "Step name filter length should be between 1 and 256 characters");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> ALIASES_TO_ADD_LENGTH =
            new ErrorCode<>("aliases_to_add_invalid_length", 400,
                "Aliases to add column name should be between 1 and 256 characters");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> ALIASES_TO_REMOVE_LENGTH =
            new ErrorCode<>("aliases_to_remove_invalid_length", 400,
                "Aliases to remove column name should be between 1 and 256 characters");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> FILE_ASSET_ID_MISSING =
            new ErrorCode<>("file_asset_id_missing", 400, "File asset id is missing");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> FILE_ASSET_NOT_FOUND =
            new ErrorCode<>("file_asset_not_found", 400, "File asset for given id does not exist");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> FILE_ASSET_NOT_ACCESSIBLE =
            new ErrorCode<>("file_asset_not_accessible", 400,
                "File asset for given id is not accessible");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> FILE_ASSET_MISSING_COLUMN =
            new ErrorCode<>("file_asset_missing_column", 400,
                "File asset for given id does not have required column", "column");

    public static final ErrorCode<
        ConditionalAliasChangeFixupTransformationValidationRestException> FILE_ASSET_INVALID_FORMAT =
            new ErrorCode<>("file_asset_invalid_format", 400,
                "File asset for given id cannot be parsed as xlsx file");

    public static final ErrorCode<ConditionalAliasChangeFixupTransformationValidationRestException> NO_OPERATION =
        new ErrorCode<>("no_operation", 400,
            "There is no operation defined. Either aliases to add or aliases to remove should be present.");

    public ConditionalAliasChangeFixupTransformationValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
