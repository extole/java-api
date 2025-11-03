package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.entity.report.audience.operation.modification.data.source.FileAssetAudienceOperationDataSource;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceResponseMapper;

@Component
public class FileAssetAudienceOperationDataSourceResponseMapper implements
    AudienceOperationDataSourceResponseMapper<FileAssetAudienceOperationDataSource,
        FileAssetAudienceOperationDataSourceResponse> {

    @Override
    public FileAssetAudienceOperationDataSourceResponse toResponse(FileAssetAudienceOperationDataSource dataSource) {
        return new FileAssetAudienceOperationDataSourceResponse(dataSource.getName(),
            dataSource.getEventColumns(),
            dataSource.getEventData(),
            Id.valueOf(dataSource.getFileAssetId().getValue()));
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.FILE_ASSET;
    }

}
