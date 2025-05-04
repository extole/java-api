package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceValidationRestException;

public class FileAssetAudienceOperationDataSourceValidationRestException
    extends AudienceOperationDataSourceValidationRestException {

    public static final ErrorCode<FileAssetAudienceOperationDataSourceValidationRestException> FILE_ASSET_NOT_FOUND =
        new ErrorCode<>("modification_audience_operation_file_asset_data_source_file_asset_not_found", 400,
            "File asset not found", "file_asset_id");

    public static final ErrorCode<FileAssetAudienceOperationDataSourceValidationRestException> MISSING_FILE_ASSET_ID =
        new ErrorCode<>("modification_audience_operation_file_asset_data_source_missing_file_asset_id", 400,
            "File asset ID is missing");

    public static final ErrorCode<
        FileAssetAudienceOperationDataSourceValidationRestException> UNSUPPORTED_FILE_ASSET_FORMAT =
            new ErrorCode<>("modification_audience_operation_file_asset_data_source_unsupported_file_asset_format", 400,
                "File asset format is not supported", "file_asset_id", "format", "supported_formats");

    public FileAssetAudienceOperationDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
