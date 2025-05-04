package com.extole.reporting.rest.impl.audience.list.request;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.AudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.UploadedAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.request.UploadedAudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListDescriptionTooLongException;
import com.extole.reporting.service.audience.list.AudienceListMapperService;
import com.extole.reporting.service.audience.list.AudienceListNameMissingException;
import com.extole.reporting.service.audience.list.AudienceListNameTooLongException;
import com.extole.reporting.service.audience.list.UploadedAudienceListAudienceNotFoundException;
import com.extole.reporting.service.audience.list.UploadedAudienceListBuilder;
import com.extole.reporting.service.audience.list.UploadedAudienceListFileAssetFormatNotSupportedException;
import com.extole.reporting.service.audience.list.UploadedAudienceListFileAssetNotFoundException;
import com.extole.reporting.service.audience.list.UploadedAudienceListMissingFileAssetIdException;
import com.extole.reporting.service.audience.list.UploadedAudienceListParameterUpdateNotAllowedException;

@Component
public class UploadedAudienceListRequestHandler
    implements AudienceListRequestHandler<UploadedAudienceListRequest, UploadedAudienceListBuilder> {

    private final AudienceListMapperService audienceListMapperService;

    @Autowired
    public UploadedAudienceListRequestHandler(AudienceListMapperService audienceListMapperService) {
        this.audienceListMapperService = audienceListMapperService;
    }

    @Override
    public AudienceListMappedResponse upload(Authorization authorization, UploadedAudienceListRequest request,
        UploadedAudienceListBuilder builder) throws AudienceListValidationRestException,
        UploadedAudienceListValidationRestException {
        request.getName().ifPresent(name -> builder.withName(name));

        request.getTags().ifPresent(tags -> {
            if (tags.isEmpty()) {
                builder.clearTags();
            } else {
                builder.withTags(tags);
            }
        });

        request.getDescription().ifPresent(description -> builder.withDescription(description));

        request.getEventColumns().ifPresent(eventColumns -> {
            if (eventColumns.isEmpty()) {
                builder.clearEventColumns();
            } else {
                builder.withEventColumns(eventColumns);
            }
        });

        request.getEventData().ifPresent(eventData -> {
            if (eventData.isEmpty()) {
                builder.clearEventData();
            } else {
                builder.withEventData(eventData);
            }
        });

        try {
            if (!Strings.isNullOrEmpty(request.getFileAssetId())) {
                builder.withFileAssetId(Id.valueOf(request.getFileAssetId()));
            }

            request.getAudienceId().ifPresent(audienceId -> builder.withAudienceId(Id.valueOf(audienceId)));

            return audienceListMapperService.toAudienceListResponse(authorization, builder.save()).get();
        } catch (AudienceListNameMissingException e) {
            throw RestExceptionBuilder
                .newBuilder(AudienceListValidationRestException.class)
                .withErrorCode(AudienceListValidationRestException.EMPTY_NAME)
                .withCause(e)
                .build();
        } catch (AudienceListNameTooLongException e) {
            throw RestExceptionBuilder
                .newBuilder(AudienceListValidationRestException.class)
                .withErrorCode(AudienceListValidationRestException.NAME_TOO_LONG)
                .withCause(e)
                .build();
        } catch (AudienceListDescriptionTooLongException e) {
            throw RestExceptionBuilder
                .newBuilder(AudienceListValidationRestException.class)
                .withErrorCode(AudienceListValidationRestException.DESCRIPTION_TOO_LONG)
                .withCause(e)
                .build();
        } catch (UploadedAudienceListParameterUpdateNotAllowedException e) {
            throw RestExceptionBuilder
                .newBuilder(UploadedAudienceListValidationRestException.class)
                .withErrorCode(UploadedAudienceListValidationRestException.UPDATE_NOT_ALLOWED)
                .addParameter("parameter", e.getParameterName())
                .withCause(e)
                .build();
        } catch (UploadedAudienceListMissingFileAssetIdException e) {
            throw RestExceptionBuilder
                .newBuilder(UploadedAudienceListValidationRestException.class)
                .withErrorCode(UploadedAudienceListValidationRestException.FILE_ASSET_ID_MISSING)
                .withCause(e)
                .build();
        } catch (UploadedAudienceListFileAssetNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(UploadedAudienceListValidationRestException.class)
                .withErrorCode(UploadedAudienceListValidationRestException.FILE_ASSET_NOT_FOUND)
                .addParameter("file_asset_id", e.getFileAssetId())
                .withCause(e)
                .build();
        } catch (UploadedAudienceListFileAssetFormatNotSupportedException e) {
            throw RestExceptionBuilder
                .newBuilder(UploadedAudienceListValidationRestException.class)
                .withErrorCode(UploadedAudienceListValidationRestException.UNSUPPORTED_FILE_ASSET_FORMAT)
                .withCause(e)
                .addParameter("file_asset_id", e.getFileAssetId())
                .addParameter("format", e.getFormat())
                .addParameter("supported_formats", e.getSupportedFormats())
                .build();
        } catch (UploadedAudienceListAudienceNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(UploadedAudienceListValidationRestException.class)
                .withErrorCode(UploadedAudienceListValidationRestException.AUDIENCE_NOT_FOUND)
                .addParameter("audience_id", e.getAudienceId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceListType getType() {
        return AudienceListType.UPLOADED;
    }
}
