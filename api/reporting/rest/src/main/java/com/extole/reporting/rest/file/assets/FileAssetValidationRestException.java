package com.extole.reporting.rest.file.assets;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FileAssetValidationRestException extends ExtoleRestException {
    public static final ErrorCode<FileAssetValidationRestException> FILE_PROCESSING_ERROR = new ErrorCode<>(
        "file_asset_file_processing_error", 400, "Failed to process uploaded file");

    public static final ErrorCode<FileAssetValidationRestException> INPUT_FILE_MISSING = new ErrorCode<>(
        "file_asset_input_file_missing", 400, "Input file is mandatory");

    public static final ErrorCode<FileAssetValidationRestException> NAME_INVALID = new ErrorCode<>(
        "file_asset_invalid_name", 400, "Name max length is 255 and should contain only alphanumeric characters",
        "name");

    public static final ErrorCode<FileAssetValidationRestException> NAME_DUPLICATED = new ErrorCode<>(
        "file_asset_duplicated_name", 400, "Duplicated file asset name", "name");

    public static final ErrorCode<FileAssetValidationRestException> TAGS_INVALID = new ErrorCode<>(
        "file_asset_invalid_tags", 400, "Tags cannot be null, empty or longer than 255 chars", "invalid_tags");

    public static final ErrorCode<FileAssetValidationRestException> DOWNLOAD_ERROR = new ErrorCode<>(
        "file_asset_download_error", 400, "File Asset can't be downloaded");

    public FileAssetValidationRestException(String uniqueId, ErrorCode<FileAssetValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
