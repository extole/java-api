package com.extole.reporting.rest.impl.batch.data.source.response;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.entity.batch.data.source.FileAssetBatchJobDataSource;
import com.extole.reporting.rest.batch.data.source.response.FileAssetBatchJobDataSourceResponse;

@Component
class FileAssetBatchJobDataSourceResponseMapper
    implements BatchJobDataSourceResponseMapper<FileAssetBatchJobDataSource, FileAssetBatchJobDataSourceResponse> {

    @Override
    public FileAssetBatchJobDataSourceResponse toResponse(FileAssetBatchJobDataSource dataSource) {
        return new FileAssetBatchJobDataSourceResponse(dataSource.getId().getValue(),
            dataSource.getFileAssetId().getValue());
    }

    @Override
    public BatchJobDataSourceType getType() {
        return BatchJobDataSourceType.FILE_ASSET;
    }
}
