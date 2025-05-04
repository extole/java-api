package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceRequestUploader;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationDataSourceBuildException;
import com.extole.reporting.service.audience.operation.AudienceOperationParameterUpdateNotAllowedException;
import com.extole.reporting.service.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceBuilder;
import com.extole.reporting.service.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceFormatNotSupportedException;
import com.extole.reporting.service.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceMissingFileAssetIdException;
import com.extole.reporting.service.audience.operation.modification.data.source.FileAssetAudienceOperationDataSourceNotFoundException;

@Component
public class FileAssetAudienceOperationDataSourceRequestUploader
    implements AudienceOperationDataSourceRequestUploader<FileAssetAudienceOperationDataSourceRequest> {

    @Override
    public void upload(AudienceOperationBuilder builder, FileAssetAudienceOperationDataSourceRequest request)
        throws FileAssetAudienceOperationDataSourceValidationRestException {
        try {
            FileAssetAudienceOperationDataSourceBuilder sourceBuilder =
                builder.withDataSource(AudienceOperationDataSourceType.FILE_ASSET);
            request.getEventColumns().ifPresent(eventColumns -> sourceBuilder.withEventColumns(eventColumns));
            request.getEventData().ifPresent(eventData -> sourceBuilder.withEventData(eventData));
            sourceBuilder.withFileAssetId(Id.valueOf(request.getFileAssetId().getValue()))
                .done();
        } catch (FileAssetAudienceOperationDataSourceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(FileAssetAudienceOperationDataSourceValidationRestException.FILE_ASSET_NOT_FOUND)
                .withCause(e)
                .addParameter("file_asset_id", e.getFileAssetId())
                .build();
        } catch (FileAssetAudienceOperationDataSourceMissingFileAssetIdException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(FileAssetAudienceOperationDataSourceValidationRestException.MISSING_FILE_ASSET_ID)
                .withCause(e)
                .build();
        } catch (FileAssetAudienceOperationDataSourceFormatNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(FileAssetAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(
                    FileAssetAudienceOperationDataSourceValidationRestException.UNSUPPORTED_FILE_ASSET_FORMAT)
                .withCause(e)
                .addParameter("file_asset_id", e.getFileAssetId())
                .addParameter("format", e.getFormat())
                .addParameter("supported_formats", e.getSupportedFormats())
                .build();
        } catch (AudienceOperationDataSourceBuildException | AudienceOperationParameterUpdateNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.FILE_ASSET;
    }

}
