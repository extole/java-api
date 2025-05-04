package com.extole.reporting.rest.impl.batch.data.source.request;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.FileAssetBatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.request.FileAssetBatchJobDataSourceRequest;
import com.extole.reporting.service.batch.BatchJobBuilder;
import com.extole.reporting.service.batch.data.source.BatchJobDataSourceEmptyIdException;
import com.extole.reporting.service.batch.data.source.BatchJobDataSourceMissingIdException;
import com.extole.reporting.service.batch.data.source.FileAssetBatchJobDataSourceBuilder;
import com.extole.reporting.service.batch.data.source.FileAssetBatchJobDataSourceNotFoundException;

@Component
class FileAssetBatchJobDataSourceRequestMapper
    implements BatchJobDataSourceRequestMapper<FileAssetBatchJobDataSourceRequest> {

    @Override
    public void upload(BatchJobBuilder builder, FileAssetBatchJobDataSourceRequest request)
        throws BatchJobDataSourceValidationRestException {
        FileAssetBatchJobDataSourceBuilder sourceBuilder =
            builder.withDataSource(com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType.FILE_ASSET);

        try {
            if (!Strings.isNullOrEmpty(request.getFileAssetId())) {
                sourceBuilder.withDataSourceId(Id.valueOf(request.getFileAssetId()));
            }
            sourceBuilder.done();
        } catch (FileAssetBatchJobDataSourceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetBatchJobDataSourceValidationRestException.class)
                .withErrorCode(FileAssetBatchJobDataSourceValidationRestException.FILE_ASSET_NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId().getValue())
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceMissingIdException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetBatchJobDataSourceValidationRestException.class)
                .withErrorCode(FileAssetBatchJobDataSourceValidationRestException.FILE_ASSET_ID_MISSING)
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceEmptyIdException e) {
            // should never happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobDataSourceType getType() {
        return BatchJobDataSourceType.FILE_ASSET;
    }
}
