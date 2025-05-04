package com.extole.reporting.rest.impl.audience.list.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.AudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.StaticAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.request.StaticAudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListDescriptionTooLongException;
import com.extole.reporting.service.audience.list.AudienceListMapperService;
import com.extole.reporting.service.audience.list.AudienceListNameMissingException;
import com.extole.reporting.service.audience.list.AudienceListNameTooLongException;
import com.extole.reporting.service.audience.list.StaticAudienceListBuilder;
import com.extole.reporting.service.audience.list.StaticAudienceListInvalidReportScopesException;
import com.extole.reporting.service.audience.list.StaticAudienceListMissingReportIdException;
import com.extole.reporting.service.audience.list.StaticAudienceListReportNotFoundException;

@Component
public class StaticAudienceListRequestHandler
    implements AudienceListRequestHandler<StaticAudienceListRequest, StaticAudienceListBuilder> {

    private final AudienceListMapperService audienceListMapperService;

    @Autowired
    public StaticAudienceListRequestHandler(AudienceListMapperService audienceListMapperService) {
        this.audienceListMapperService = audienceListMapperService;
    }

    @Override
    public AudienceListMappedResponse upload(Authorization authorization, StaticAudienceListRequest request,
        StaticAudienceListBuilder builder) throws AudienceListValidationRestException,
        StaticAudienceListValidationRestException {
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

        if (request.getReportId() != null) {
            builder.withReportId(Id.valueOf(request.getReportId()));
        }

        try {
            return audienceListMapperService.toAudienceListResponse(authorization, builder.save()).get();
        } catch (StaticAudienceListReportNotFoundException e) {
            throw RestExceptionBuilder
                .newBuilder(StaticAudienceListValidationRestException.class)
                .withErrorCode(StaticAudienceListValidationRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", request.getReportId())
                .withCause(e)
                .build();
        } catch (StaticAudienceListInvalidReportScopesException e) {
            throw RestExceptionBuilder
                .newBuilder(StaticAudienceListValidationRestException.class)
                .withErrorCode(StaticAudienceListValidationRestException.REPORT_NOT_ACCESSIBLE)
                .addParameter("report_id", request.getReportId())
                .withCause(e)
                .build();
        } catch (StaticAudienceListMissingReportIdException e) {
            throw RestExceptionBuilder
                .newBuilder(StaticAudienceListValidationRestException.class)
                .withErrorCode(StaticAudienceListValidationRestException.REPORT_ID_MISSING)
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
        }
    }

    @Override
    public AudienceListType getType() {
        return AudienceListType.STATIC;
    }
}
