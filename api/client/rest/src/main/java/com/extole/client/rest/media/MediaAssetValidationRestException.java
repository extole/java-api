package com.extole.client.rest.media;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class MediaAssetValidationRestException extends ExtoleRestException {
    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_NAME_MISSING =
        new ErrorCode<>("media_asset_name_missing", 400, "Media asset name is missing");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("media_asset_name_length_out_of_range", 400,
            "Media asset name length must be between 1 and 255 characters");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("media_asset_name_contains_illegal_character", 400,
            "Media asset name can only contain alphanumeric, dash, dot and underscore characters", "name");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_NAME_DUPLICATED = new ErrorCode<>(
        "media_asset_name_duplicated", 400, "Media asset with this name already exists for the current client", "name");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_CONTENT_MISSING =
        new ErrorCode<>("media_asset_content_missing", 400, "Media asset content is missing");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_CONTENT_SIZE_TOO_BIG =
        new ErrorCode<>("media_asset_content_size_too_big", 400, "Media asset content size is too big (max_size=3MB)",
            "size");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_CONTENT_UPLOAD_ERROR =
        new ErrorCode<>("media_asset_content_upload_error", 400, "Media asset content upload error");

    public static final ErrorCode<MediaAssetValidationRestException> MEDIA_ASSET_INVALID_JAVASCRIPT =
        new ErrorCode<>("media_asset_invalid_javascript", 400, "Media asset javascript content is invalid",
            "validation_errors");

    public MediaAssetValidationRestException(String uniqueId, ErrorCode<MediaAssetValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
