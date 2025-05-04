package com.extole.reporting.rest.audience.list;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UploadedAudienceListValidationRestException extends ExtoleRestException {

    public static final ErrorCode<UploadedAudienceListValidationRestException> FILE_ASSET_ID_MISSING =
        new ErrorCode<>("uploaded_audience_list_file_asset_id_missing", 400, "FileAsset id is mandatory");

    public static final ErrorCode<UploadedAudienceListValidationRestException> FILE_ASSET_NOT_FOUND =
        new ErrorCode<>("uploaded_audience_list_file_asset_not_found", 400, "FileAsset not found",
            "file_asset_id");

    public static final ErrorCode<UploadedAudienceListValidationRestException> UNSUPPORTED_FILE_ASSET_FORMAT =
        new ErrorCode<>("unsupported_file_asset_format", 400, "The provided file asset has an unsupported format",
            "file_asset_id", "format", "supported_formats");

    public static final ErrorCode<UploadedAudienceListValidationRestException> UPDATE_NOT_ALLOWED =
        new ErrorCode<>("uploaded_audience_list_update_not_allowed", 400, "FileAssetId update not allowed",
            "parameter");

    public static final ErrorCode<UploadedAudienceListValidationRestException> AUDIENCE_NOT_FOUND =
        new ErrorCode<>("uploaded_audience_list_audience_not_found", 400, "Audience not found",
            "audience_id");

    public UploadedAudienceListValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
