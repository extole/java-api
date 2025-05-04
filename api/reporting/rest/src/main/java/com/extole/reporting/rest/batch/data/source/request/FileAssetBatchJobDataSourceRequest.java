package com.extole.reporting.rest.batch.data.source.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

public class FileAssetBatchJobDataSourceRequest extends BatchJobDataSourceRequest {
    static final String DATA_SOURCE_TYPE = "FILE_ASSET";

    private static final String FILE_ASSET_ID = "file_asset_id";

    private final String fileAssetId;

    public FileAssetBatchJobDataSourceRequest(@JsonProperty(FILE_ASSET_ID) String fileAssetId) {
        super(BatchJobDataSourceType.FILE_ASSET);
        this.fileAssetId = fileAssetId;
    }

    @JsonProperty(FILE_ASSET_ID)
    public String getFileAssetId() {
        return fileAssetId;
    }
}
