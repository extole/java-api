package com.extole.reporting.rest.impl.audience.list.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.AudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.DynamicAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.request.DynamicAudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListDescriptionTooLongException;
import com.extole.reporting.service.audience.list.AudienceListMapperService;
import com.extole.reporting.service.audience.list.AudienceListNameMissingException;
import com.extole.reporting.service.audience.list.AudienceListNameTooLongException;
import com.extole.reporting.service.audience.list.AudienceListStateNotFoundException;
import com.extole.reporting.service.audience.list.DynamicAudienceListBuilder;
import com.extole.reporting.service.audience.list.DynamicAudienceListInvalidReportScopesException;
import com.extole.reporting.service.audience.list.DynamicAudienceListMissingReportRunnerIdException;
import com.extole.reporting.service.audience.list.DynamicAudienceListReportRunnerNotFoundException;

@Component
public class DynamicAudienceListRequestHandler
    implements AudienceListRequestHandler<DynamicAudienceListRequest, DynamicAudienceListBuilder> {

    private final AudienceListMapperService audienceListMapperService;

    @Autowired
    public DynamicAudienceListRequestHandler(AudienceListMapperService audienceListMapperService) {
        this.audienceListMapperService = audienceListMapperService;
    }

    @Override
    public AudienceListMappedResponse upload(Authorization authorization, DynamicAudienceListRequest request,
        DynamicAudienceListBuilder builder) throws DynamicAudienceListValidationRestException,
        AudienceListValidationRestException {
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

        if (request.getReportRunnerId() != null) {
            builder.withReportRunnerId(Id.valueOf(request.getReportRunnerId()));
        }

        try {
            return audienceListMapperService.toAudienceListResponse(authorization, builder.save()).get();
        } catch (DynamicAudienceListReportRunnerNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(DynamicAudienceListValidationRestException.class)
                .withErrorCode(DynamicAudienceListValidationRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", request.getReportRunnerId())
                .withCause(e)
                .build();
        } catch (DynamicAudienceListInvalidReportScopesException e) {
            throw RestExceptionBuilder
                .newBuilder(DynamicAudienceListValidationRestException.class)
                .withErrorCode(DynamicAudienceListValidationRestException.REPORT_RUNNER_NOT_ACCESSIBLE)
                .addParameter("report_runner_id", request.getReportRunnerId())
                .withCause(e)
                .build();
        } catch (DynamicAudienceListMissingReportRunnerIdException e) {
            throw RestExceptionBuilder
                .newBuilder(DynamicAudienceListValidationRestException.class)
                .withErrorCode(DynamicAudienceListValidationRestException.REPORT_RUNNER_ID_MISSING)
                .withCause(e)
                .build();
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
        } catch (AudienceListStateNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceListType getType() {
        return AudienceListType.DYNAMIC;
    }
}
