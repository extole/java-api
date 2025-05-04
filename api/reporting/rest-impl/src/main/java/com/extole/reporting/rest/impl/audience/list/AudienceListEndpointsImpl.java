package com.extole.reporting.rest.impl.audience.list;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.request.resolver.ResolvesPolymorphicType;
import com.extole.id.Id;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.entity.report.audience.list.AudienceList;
import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.entity.report.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListQueryParams;
import com.extole.reporting.rest.audience.list.AudienceListRestException;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.AudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.AudienceListsEndpoints;
import com.extole.reporting.rest.audience.list.AudienceOrderBy;
import com.extole.reporting.rest.audience.list.AudienceOrderDirection;
import com.extole.reporting.rest.audience.list.DynamicAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.StaticAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.UploadedAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.request.AudienceListRequest;
import com.extole.reporting.rest.audience.list.response.AudienceListDebugResponse;
import com.extole.reporting.rest.audience.list.response.AudienceListResponse;
import com.extole.reporting.rest.impl.audience.list.request.AudienceListRequestMappersRepository;
import com.extole.reporting.rest.impl.audience.list.request.AudienceListRequestResolver;
import com.extole.reporting.rest.impl.audience.list.response.AudienceListDebugResponseMapper;
import com.extole.reporting.rest.impl.audience.list.response.AudienceListResponseMappersRepository;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.audience.list.AudienceListBuilder;
import com.extole.reporting.service.audience.list.AudienceListContentNotAvailableException;
import com.extole.reporting.service.audience.list.AudienceListException;
import com.extole.reporting.service.audience.list.AudienceListFormatNotAvailableException;
import com.extole.reporting.service.audience.list.AudienceListInfo;
import com.extole.reporting.service.audience.list.AudienceListNotFoundException;
import com.extole.reporting.service.audience.list.AudienceListNotReadyException;
import com.extole.reporting.service.audience.list.AudienceListQueryBuilder;
import com.extole.reporting.service.audience.list.AudienceListRefreshException;
import com.extole.reporting.service.audience.list.AudienceListService;
import com.extole.reporting.service.audience.list.AudienceListSourceNotFoundException;
import com.extole.reporting.service.audience.list.AudienceListStateNotFoundException;
import com.extole.reporting.service.audience.list.UploadedAudienceListRefreshException;
import com.extole.reporting.service.report.ReportContentDownloadException;
import com.extole.reporting.service.report.ReportContentFormatNotFoundException;
import com.extole.reporting.service.report.ReportContentNotFoundException;
import com.extole.reporting.service.report.ReportService;

@Provider
public class AudienceListEndpointsImpl implements AudienceListsEndpoints {

    private static final int DEFAULT_LIMIT = 100;
    private static final String CONTENT_DISPOSITION_FORMATTER = "attachment; filename = report-%s-%s.%s";

    private final AudienceListService audienceListService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final AudienceListRequestMappersRepository uploadersRepository;
    private final AudienceListResponseMappersRepository responseMappers;
    private final AudienceListDebugResponseMapper audienceListDebugResponseMapper;
    private final ReportService reportService;

    @Inject
    public AudienceListEndpointsImpl(AudienceListService audienceListService,
        ClientAuthorizationProvider authorizationProvider,
        AudienceListRequestMappersRepository uploadersRepository,
        AudienceListResponseMappersRepository responseMappers,
        AudienceListDebugResponseMapper audienceListDebugResponseMapper,
        ReportService reportService) {
        this.audienceListService = audienceListService;
        this.authorizationProvider = authorizationProvider;
        this.uploadersRepository = uploadersRepository;
        this.responseMappers = responseMappers;
        this.audienceListDebugResponseMapper = audienceListDebugResponseMapper;
        this.reportService = reportService;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public AudienceListResponse get(String accessToken, String audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListMappedResponse audienceList =
                audienceListService.getById(authorization, Id.valueOf(audienceId));
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<AudienceListResponse> list(String accessToken, AudienceListQueryParams queryParams,
        ZoneId timeZone) throws UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListQueryBuilder queryBuilder = audienceListService.list(authorization);

            queryParams.getName().filter(name -> !Strings.isNullOrEmpty(name)).ifPresent(queryBuilder::withName);
            if (!queryParams.getTags().isEmpty()) {
                queryBuilder.withTags(queryParams.getTags());
            }
            if (!queryParams.getStates().isEmpty()) {
                queryBuilder.withStates(queryParams.getStates().stream()
                    .map(com.extole.reporting.rest.audience.list.AudienceListState::name)
                    .map(AudienceListState.State::valueOf)
                    .collect(Collectors.toSet()));
            }
            queryParams.getType().map(AudienceListType::name)
                .map(com.extole.reporting.entity.report.audience.list.AudienceListType::valueOf)
                .ifPresent(queryBuilder::withType);
            if (queryParams.getIncludeArchived().isPresent()
                && Boolean.TRUE.equals(queryParams.getIncludeArchived().get())) {
                queryBuilder.includeArchived();
            }
            queryParams.getLimit().ifPresent(queryBuilder::withLimit);
            queryParams.getOffset().ifPresent(queryBuilder::withOffset);
            queryParams.getOrderBy().map(AudienceOrderBy::name)
                .map(com.extole.reporting.service.audience.list.AudienceOrderBy::valueOf)
                .ifPresent(queryBuilder::withOrderBy);
            queryParams.getOrder().map(AudienceOrderDirection::name)
                .map(com.extole.reporting.service.audience.list.AudienceOrderDirection::valueOf)
                .ifPresent(queryBuilder::withOrder);

            List<AudienceListResponse> audiencesListResponses = new ArrayList<>();
            for (AudienceListMappedResponse audienceList : queryBuilder.execute()) {
                AudienceListResponse audienceListResponse = responseMappers
                    .getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                    .toResponse(audienceList, timeZone);
                audiencesListResponses.add(audienceListResponse);
            }
            return audiencesListResponses;
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AudienceListResponse create(String accessToken, AudienceListRequest request,
        ZoneId timeZone) throws UserAuthorizationRestException, AudienceListValidationRestException,
        DynamicAudienceListValidationRestException, StaticAudienceListValidationRestException,
        UploadedAudienceListValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListBuilder builder = audienceListService.create(authorization,
                com.extole.reporting.entity.report.audience.list.AudienceListType.valueOf(request.getType().name()));
            AudienceListMappedResponse audienceList =
                uploadersRepository.getUploader(request.getType()).upload(authorization, request, builder);
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    @ResolvesPolymorphicType(resolver = AudienceListRequestResolver.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AudienceListResponse update(String accessToken, String audienceId, AudienceListRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException, AudienceListValidationRestException,
        DynamicAudienceListValidationRestException, StaticAudienceListValidationRestException,
        UploadedAudienceListValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListBuilder builder = audienceListService.update(authorization, Id.valueOf(audienceId));
            AudienceListMappedResponse audienceList =
                uploadersRepository.getUploader(request.getType())
                    .upload(authorization, request, builder);
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException | AudienceListStateNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public AudienceListResponse archive(String accessToken, String audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListMappedResponse audienceList =
                audienceListService.archive(authorization, Id.valueOf(audienceId));
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListStateNotFoundException | AudienceListException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public Response download(String accessToken, String contentType, String audienceId,
        Optional<String> format, Optional<Integer> limit, Optional<Integer> offset, ZoneId timeZone)
        throws AudienceListRestException, UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            Optional<Report.Format> reportFormat = Optional.empty();
            if (format.isPresent() && !Strings.isNullOrEmpty(format.get())) {
                reportFormat = Optional.of(getFormat(format.get().replace(".", ""), Id.valueOf(audienceId)));
            }

            AudienceListInfo audienceListInfo;
            if (reportFormat.isPresent()) {
                audienceListInfo = audienceListService
                    .getDownloadInfo(authorization, Id.valueOf(audienceId), reportFormat.get());
            } else {
                audienceListInfo = audienceListService
                    .getDownloadInfo(authorization, Id.valueOf(audienceId));
            }
            return download(Id.valueOf(audienceId), audienceListInfo.getFormat(), limit, offset,
                authorization, audienceListInfo);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListContentNotAvailableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.CONTENT_NOT_AVAILABLE)
                .addParameter("audience_list_id", audienceId)
                .addParameter("format", format)
                .withCause(e).build();
        } catch (AudienceListFormatNotAvailableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.FORMAT_NOT_AVAILABLE)
                .addParameter("audience_list_id", audienceId)
                .addParameter("format", format)
                .withCause(e).build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public AudienceListResponse snapshot(String accessToken, String audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListMappedResponse audienceList =
                audienceListService.snapshot(authorization, Id.valueOf(audienceId));
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListContentNotAvailableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.CONTENT_NOT_AVAILABLE)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListNotReadyException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.SNAPSHOT_NOT_SUPPORTED)
                .addParameter("audience_list_id", e.getAudienceListId())
                .withCause(e).build();
        } catch (AudienceListException | AudienceListStateNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public AudienceListResponse refresh(String accessToken, String audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListMappedResponse audienceList =
                audienceListService.refresh(authorization, Id.valueOf(audienceId));
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListSourceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.SOURCE_NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e)
                .build();
        } catch (UploadedAudienceListRefreshException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.UPLOADED_REFRESH_ERROR)
                .addParameter("audience_list_id", audienceId)
                .withCause(e)
                .build();
        } catch (AudienceListRefreshException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.REFRESH_ERROR)
                .addParameter("audience_list_id", audienceId)
                .withCause(e)
                .build();
        } catch (AudienceListException | AudienceListStateNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public AudienceListDebugResponse readDebug(String accessToken, String audienceId)
        throws UserAuthorizationRestException, AudienceListRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            AudienceListMappedResponse audienceList =
                audienceListService.getById(authorization, Id.valueOf(audienceId));
            return audienceListDebugResponseMapper.toResponse(audienceList);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public AudienceListResponse cancel(String accessToken, String audienceId, ZoneId timeZone)
        throws UserAuthorizationRestException, AudienceListRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            AudienceListMappedResponse audienceList =
                audienceListService.cancel(authorization, Id.valueOf(audienceId));
            return responseMappers.getMapper(AudienceListType.valueOf(audienceList.getType().name()))
                .toResponse(audienceList, timeZone);
        } catch (UserAuthorizationRestException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (AudienceListNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.NOT_FOUND)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListContentNotAvailableException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.CONTENT_NOT_AVAILABLE)
                .addParameter("audience_list_id", audienceId)
                .withCause(e).build();
        } catch (AudienceListException | AudienceListStateNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private Report.Format getFormat(String format, Id<AudienceList> audienceListId) throws AudienceListRestException {
        try {
            return Report.Format.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.FORMAT_NOT_SUPPORTED)
                .addParameter("audience_list_id", audienceListId)
                .addParameter("format", format)
                .withCause(e)
                .build();
        }
    }

    private Response download(Id<AudienceList> audienceListId, Report.Format reportFormat,
        Optional<Integer> limit, Optional<Integer> offset, Authorization authorization, AudienceListInfo formatInfo)
        throws AudienceListRestException {

        boolean paginate = limit.isPresent() || offset.isPresent();

        if (!formatInfo.isPreviewAvailable() && paginate) {
            throw RestExceptionBuilder.newBuilder(AudienceListRestException.class)
                .withErrorCode(AudienceListRestException.PREVIEW_NOT_AVAILABLE)
                .addParameter("audience_list_id", audienceListId)
                .addParameter("report_id", formatInfo.getReport().getId())
                .addParameter("format", reportFormat.name())
                .build();
        }
        StreamingOutput streamer =
            outputStream -> {
                try {
                    if (paginate) {
                        int limitValue = limit.orElse(Integer.valueOf(DEFAULT_LIMIT)).intValue();
                        int offsetValue = offset.orElse(Integer.valueOf(0)).intValue();
                        reportService.downloadReportSummary(authorization, formatInfo.getReport().getId(), reportFormat,
                            limitValue, offsetValue, outputStream);
                    } else {
                        reportService.downloadReport(authorization, formatInfo.getReport().getId(), reportFormat,
                            outputStream);
                    }
                } catch (AuthorizationException | ReportNotFoundException | ReportContentNotFoundException
                    | ReportContentFormatNotFoundException | ReportContentDownloadException e) {
                    throw new ReportRuntimeException(e);
                }
            };

        Response.ResponseBuilder responseBuilder = Response.ok(streamer, formatInfo.getFormat().getMimeType());
        if (!paginate) {
            responseBuilder.header(HttpHeaders.CONTENT_LENGTH, Long.valueOf(formatInfo.getContentLength()));
        }
        responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
            String.format(CONTENT_DISPOSITION_FORMATTER, formatInfo.getName().toLowerCase(),
                formatInfo.getReport().getId(), formatInfo.getFormat().getExtension()));

        return responseBuilder.build();
    }
}
