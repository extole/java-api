package com.extole.reporting.rest.batch.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class FileAssetBatchJobDataSourceValidationRestException extends BatchJobDataSourceValidationRestException {

    public static final ErrorCode<FileAssetBatchJobDataSourceValidationRestException> FILE_ASSET_NOT_FOUND =
        new ErrorCode<>("batch_job_data_source_file_asset_not_found", 400,
            "FileAsset not found", "file_asset_id");

    public static final ErrorCode<FileAssetBatchJobDataSourceValidationRestException> FILE_ASSET_ID_MISSING =
        new ErrorCode<>("batch_job_data_source_file_asset_id_missing", 400, "FileAsset id is missing");

    public static final ErrorCode<FileAssetBatchJobDataSourceValidationRestException> FILE_ASSET_FORMAT_NOT_SUPPORTED =
        new ErrorCode<>("batch_job_data_source_file_asset_format_not_supported", 400,
            "FileAsset format is not supported", "format", "supported_formats");

    public FileAssetBatchJobDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
