package com.extole.reporting.rest.impl.report.runner;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.lang.date.DateTimeRange;
import com.extole.common.lang.date.DateTimeRangeBuilder;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.rest.support.request.resolver.ResolvesPolymorphicType;
import com.extole.id.Id;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.ClientService;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.entity.report.ReportOrderBy;
import com.extole.reporting.entity.report.ReportOrderDirection;
import com.extole.reporting.entity.report.ReportResult;
import com.extole.reporting.entity.report.ReportResult.Status;
import com.extole.reporting.entity.report.runner.ReportRunner;
import com.extole.reporting.entity.report.runner.ReportRunnerOrder;
import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.entity.report.runner.ReportSlot;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.rest.impl.report.runner.mappers.ReportRunnerResponseMapper;
import com.extole.reporting.rest.impl.report.runner.mappers.ReportSlotResponseMapper;
import com.extole.reporting.rest.impl.report.runner.resolver.ReportRunnerRequestResolver;
import com.extole.reporting.rest.impl.report.runner.uploaders.ReportRunnerUploader;
import com.extole.reporting.rest.report.execution.ReportDownloadRestException;
import com.extole.reporting.rest.report.execution.ReportNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportRestException;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;
import com.extole.reporting.rest.report.runner.BaseReportRunnerReportResponse;
import com.extole.reporting.rest.report.runner.ReportRunnerCreateRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerEndpoints;
import com.extole.reporting.rest.report.runner.ReportRunnerQueryRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerResponse;
import com.extole.reporting.rest.report.runner.ReportRunnerRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerSlotsRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerUpdateRequest;
import com.extole.reporting.rest.report.runner.ReportRunnerValidationRestException;
import com.extole.reporting.rest.report.runner.ReportRunnersListRequest;
import com.extole.reporting.service.ReportInvalidParametersException;
import com.extole.reporting.service.ReportMissingParametersException;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.ReportingServiceException;
import com.extole.reporting.service.report.ReportContentDownloadException;
import com.extole.reporting.service.report.ReportContentFormatNotFoundException;
import com.extole.reporting.service.report.ReportContentNotFoundException;
import com.extole.reporting.service.report.ReportFormatInfo;
import com.extole.reporting.service.report.ReportFormatNotSupportedException;
import com.extole.reporting.service.report.ReportService;
import com.extole.reporting.service.report.ReportSftpNotSupportedException;
import com.extole.reporting.service.report.runner.AggregationStatus;
import com.extole.reporting.service.report.runner.NoExecutionTimeRangesException;
import com.extole.reporting.service.report.runner.PauseStatus;
import com.extole.reporting.service.report.runner.ReportRunnerFormatNotSupportedException;
import com.extole.reporting.service.report.runner.ReportRunnerInvalidParametersException;
import com.extole.reporting.service.report.runner.ReportRunnerInvalidScopesException;
import com.extole.reporting.service.report.runner.ReportRunnerMergeEmptyFormatException;
import com.extole.reporting.service.report.runner.ReportRunnerMissingNameException;
import com.extole.reporting.service.report.runner.ReportRunnerMissingParametersException;
import com.extole.reporting.service.report.runner.ReportRunnerNotFoundException;
import com.extole.reporting.service.report.runner.ReportRunnerPausedException;
import com.extole.reporting.service.report.runner.ReportRunnerQueryBuilder;
import com.extole.reporting.service.report.runner.ReportRunnerReportType;
import com.extole.reporting.service.report.runner.ReportRunnerReportTypeMissingException;
import com.extole.reporting.service.report.runner.ReportRunnerReportTypeNotFoundException;
import com.extole.reporting.service.report.runner.ReportRunnerService;
import com.extole.reporting.service.report.runner.ReportRunnerSlotNotSupportedException;
import com.extole.reporting.service.report.runner.ReportRunnerUpdateManagedByGitException;
import com.extole.reporting.service.report.runner.ReportSlotsByReportRunnerListQueryBuilder;

@Provider
public class ReportRunnerEndpointsImpl implements ReportRunnerEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ReportRunnerEndpointsImpl.class);

    private static final String REPORT_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s.%s";
    private static final String FILENAME_FORMATTER = "report-%s-%s";
    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportRunnerService reportRunnerService;
    private final ReportService reportService;

    private final ClientService clientService;
    private final ReportRunnerUploadersRepository reportRunnerUploadersRepository;
    private final ReportRunnerResponseMappersRepository reportRunnerResponseMappersRepository;
    private final ReportResponseMapper reportResponseMapper;

    private final ReportSlotResponseMapper reportSlotResponseMapper;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ReportRunnerEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        ReportRunnerService reportRunnerService,
        ReportService reportService,
        ClientService clientService,
        ReportRunnerUploadersRepository reportRunnerUploadersRepository,
        ReportRunnerResponseMappersRepository reportRunnerResponseMappersRepository,
        ReportResponseMapper reportResponseMapper,
        ReportSlotResponseMapper reportSlotResponseMapper,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportRunnerService = reportRunnerService;
        this.reportService = reportService;
        this.clientService = clientService;
        this.reportRunnerUploadersRepository = reportRunnerUploadersRepository;
        this.reportRunnerResponseMappersRepository = reportRunnerResponseMappersRepository;
        this.reportResponseMapper = reportResponseMapper;
        this.reportSlotResponseMapper = reportSlotResponseMapper;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public ReportRunnerResponse createReportRunner(String accessToken, ReportRunnerCreateRequest request,
        ZoneId timezone) throws UserAuthorizationRestException, ReportRunnerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        LOG.info("Creating report runner {} for client {} and token {}", request.getName(),
            authorization.getClientId(), accessToken);
        try {
            ReportRunner reportRunner = upload(new CreateReportRunnerSupplier(request, authorization));
            return toReportRunnerResponse(authorization, reportRunner, timezone);
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e)
                .build();
        }
    }

    @Override
    @ResolvesPolymorphicType(resolver = ReportRunnerRequestResolver.class)
    public ReportRunnerResponse updateReportRunner(String accessToken, String reportRunnerId,
        ReportRunnerUpdateRequest request, ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerValidationRestException, ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        LOG.info("Creating report runner {} for client {} and token {}", request.getName(),
            authorization.getClientId(), accessToken);

        try {
            ReportRunner reportRunner =
                upload(new UpdateReportRunnerSupplier(authorization, Id.valueOf(reportRunnerId), request));
            return toReportRunnerResponse(authorization, reportRunner, timezone);
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId()).withCause(e)
                .build();
        }
    }

    @Override
    public ReportRunnerResponse getReportRunner(String accessToken, String reportRunnerId,
        ZoneId timezone) throws UserAuthorizationRestException, ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportRunner reportRunner = reportRunnerService.getById(authorization, Id.valueOf(reportRunnerId));
            return toReportRunnerResponse(authorization, reportRunner, timezone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId().getValue())
                .withCause(e).build();
        }
    }

    @Override
    public ReportRunnerResponse deleteReportRunner(String accessToken, String reportRunnerId,
        ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportRunner reportRunner =
                reportRunnerService.delete(authorization, Id.valueOf(reportRunnerId));
            return toReportRunnerResponse(authorization, reportRunner, timezone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (ReportRunnerUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_LOCKED)
                .withCause(e).build();
        }
    }

    @Override
    public BaseReportRunnerReportResponse run(String accessToken, String reportRunnerId,
        Optional<ReportRunnerSlotsRequest> request, ZoneId timeZone) throws UserAuthorizationRestException,
        ReportRunnerRestException, ReportValidationRestException, ReportRunnerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ZoneId clientTimezone = getClientTimezone(authorization.getClientId());

            Optional<DateTimeRange> slot = Optional.empty();
            if (request.isPresent()) {
                DateTimeRange dateTimeRange = new DateTimeRangeBuilder()
                    .withRangeString(request.get().getSlot())
                    .withDefaultTimezone(clientTimezone)
                    .build();
                slot = Optional.of(new DateTimeRange(dateTimeRange.getStartDate().withZoneSameInstant(clientTimezone),
                    dateTimeRange.getEndDate().withZoneSameInstant(clientTimezone)));
            }

            ReportSlot reportSlot =
                reportRunnerService.executeReport(authorization, Id.valueOf(reportRunnerId), slot.isEmpty(), slot);
            return reportSlotResponseMapper.toResponse(authorization, reportSlot, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportMissingParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_MISSING_PARAMETERS)
                .addParameter("parameters", e.getMissingParameters())
                .withCause(e).build();
        } catch (ReportInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_PARAMETER)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (ReportFormatNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_FORMATS)
                .addParameter("formats", e.getFormats())
                .withCause(e).build();
        } catch (ReportSftpNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_SFTP_KEY_MISSING)
                .withCause(e).build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_SFTP_SERVER)
                .addParameter("sftp_server_id", e.getSftpDestinationId())
                .withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (NoExecutionTimeRangesException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_NO_EXECUTION_TIME_RANGE)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (ReportRunnerPausedException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_PAUSED)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_SLOT)
                .addParameter("slot", request.get().getSlot())
                .withCause(e).build();
        } catch (ReportRunnerSlotNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_SLOTS_NOT_SUPPORTED)
                .withCause(e).build();
        } catch (ClientNotFoundException | ReportingServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public List<? extends ReportRunnerResponse> getReportRunners(String accessToken, ReportRunnersListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportRunnersListRequest.builder().build();
            }
            ReportRunnerQueryBuilder queryBuilder = reportRunnerService.getByQuery(authorization);
            if (request.getType().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withReportRunnerType(parseReportRunnerType(request.getType().get()));
            }
            request.getDisplayName().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withDisplayName);
            request.getReportTypeName().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withReportTypeName);
            request.getHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withHavingAnyTags);
            request.getHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withHavingAllTags);
            request.getExcludeHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withExcludeHavingAnyTags);
            request.getExcludeHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(queryBuilder::withExcludeHavingAllTags);
            request.getSearchQuery().filter(StringUtils::isNotEmpty).ifPresent(queryBuilder::withSearchQuery);
            if (request.getPauseStatus().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withPauseStatuses(parsePauseStatuses(request.getPauseStatus().get()));
            }
            if (request.getAggregationStatus().filter(StringUtils::isNotEmpty).isPresent()) {
                queryBuilder.withAggregationStatuses(parseAggregationStatuses(request.getAggregationStatus().get()));
            }
            if (!request.getUserIds().isEmpty()) {
                queryBuilder.withUserIds(request.getUserIds());
            }

            queryBuilder
                .withOffset(
                    parseOffset(request.getOffset().orElse(BigDecimal.ZERO.toString()), BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(request.getLimit().orElse(String.valueOf(DEFAULT_LIMIT)), DEFAULT_LIMIT))
                .withOrder(request.getOrder().filter(StringUtils::isNotEmpty).map(ReportRunnerOrder.Direction::valueOf)
                    .orElse(ReportRunnerOrder.Direction.DESCENDING))
                .withOrderBy(request.getOrderBy().filter(StringUtils::isNotEmpty).map(ReportRunnerOrder.Field::valueOf)
                    .orElse(ReportRunnerOrder.Field.CREATED_DATE));
            return toReportRunnersResponse(authorization, queryBuilder.list(), request.getTimezone().orElse(null));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public List<BaseReportRunnerReportResponse> getReports(String accessToken, String reportRunnerId, String types,
        String statuses, String offset, String limit, ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException,
        ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            Set<ReportRunnerReportType> filterTypes = parseReportRunnerReportTypes(types);
            Set<ReportResult.Status> filterStatuses = parseSlotStatuses(statuses);
            ReportSlotsByReportRunnerListQueryBuilder queryBuilder = reportRunnerService
                .listReportSlots(authorization, Id.valueOf(reportRunnerId))
                .withOffset(parseOffset(offset, BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(limit, DEFAULT_LIMIT));
            if (!filterTypes.isEmpty()) {
                queryBuilder.withTypes(filterTypes);
            }
            if (!filterStatuses.isEmpty()) {
                queryBuilder.withStatuses(filterStatuses);
            }

            return queryBuilder.execute()
                .stream()
                .map(reportSlot -> reportSlotResponseMapper.toResponse(authorization, reportSlot, requestHeaders,
                    timezone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId()).withCause(e)
                .build();
        }
    }

    @Override
    public List<? extends ReportRunnerResponse> getTemplateReportRunners(String accessToken, ZoneId timeZone)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return toReportRunnersResponse(authorization,
                reportRunnerService.getTemplateReportRunners(authorization), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ReportRunnerResponse duplicate(String accessToken, String reportRunnerId, Boolean allowDuplicate,
        ReportRunnerUpdateRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportRunner reportRunner =
                upload(new DuplicateReportRunnerSupplier(authorization, Id.valueOf(reportRunnerId),
                    Optional.ofNullable(allowDuplicate).orElse(Boolean.TRUE).booleanValue(),
                    request));
            return toReportRunnerResponse(authorization, reportRunner, timeZone);
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId()).withCause(e)
                .build();
        }
    }

    @Override
    public List<ReportResponse> getRollingReports(String accessToken, String reportRunnerId, String statuses,
        String offset, String limit, ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException {
        return getAccumulatingReports(accessToken, reportRunnerId, statuses, offset, limit, timezone);
    }

    @Override
    public List<ReportResponse> getAccumulatingReports(String accessToken, String reportRunnerId, String statuses,
        String offset, String limit, ZoneId timezone)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRunnerQueryRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return reportRunnerService
                .listAccumulatingReports(authorization, Id.valueOf(reportRunnerId))
                .withOffset(parseOffset(offset, BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(limit, DEFAULT_LIMIT))
                .withOrder(ReportOrderDirection.DESCENDING)
                .withOrderBy(ReportOrderBy.DATE_RUN)
                .withStatuses(parseStatuses(statuses))
                .execute()
                .stream()
                .map(report -> reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timezone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ReportResponse getLatestReport(String accessToken, String reportRunnerId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<Report> report = reportRunnerService.getLatestReport(authorization, Id.valueOf(reportRunnerId));
            if (report.isPresent()) {
                return reportResponseMapper.toReportResponse(authorization, report.get(), requestHeaders, timeZone);
            } else {
                throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                    .withErrorCode(ReportRunnerRestException.LATEST_REPORT_NOT_FOUND)
                    .addParameter("report_runner_id", reportRunnerId).build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId()).withCause(e)
                .build();
        }
    }

    @Override
    public Response downloadLatestReport(String accessToken, String contentType, String format, String reportRunnerId,
        String limit, String offset, String filename)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException, ReportRunnerRestException,
        ReportNotFoundRestException, ReportDownloadRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Optional<Report> report = Optional.empty();
        try {
            report = reportRunnerService.getLatestReport(authorization, Id.valueOf(reportRunnerId));

            if (report.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                    .withErrorCode(ReportRunnerRestException.LATEST_REPORT_NOT_FOUND)
                    .addParameter("report_runner_id", reportRunnerId).build();
            }
            return downloadReport(Optional.ofNullable(contentType), Optional.ofNullable(format),
                Optional.ofNullable(limit), Optional.ofNullable(offset), Optional.ofNullable(filename), authorization,
                report.get());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", e.getReportRunnerId()).withCause(e)
                .build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportRunnerId)
                .withCause(e)
                .build();
        } catch (ReportContentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                .addParameter("report_id", reportRunnerId)
                .withCause(e)
                .build();
        } catch (ReportContentFormatNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND)
                .addParameter("report_id", reportRunnerId)
                .addParameter("format", contentType)
                .withCause(e)
                .build();
        } catch (ReportRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthorizationException) {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.LATEST_REPORT_NOT_FOUND)
                    .addParameter("report_id", report.get().getId())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                    .addParameter("report_id", report.get().getId())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentFormatNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND)
                    .addParameter("report_id", report.get().getId())
                    .addParameter("format", contentType)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentDownloadException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_DOWNLOADED)
                    .addParameter("report_id", report.get().getId())
                    .withCause(cause)
                    .build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause)
                    .build();
            }
        }
    }

    private Set<Status> parseStatuses(@Nullable String statuses) throws ReportRunnerQueryRestException {
        Set<Status> result = Sets.newHashSet();
        for (String status : Strings.nullToEmpty(statuses).split(",")) {
            if (Strings.isNullOrEmpty(status)) {
                continue;
            }
            try {
                result.add(Status.valueOf(status));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_STATUS)
                    .addParameter("status", status)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private Set<ReportRunnerReportType> parseReportRunnerReportTypes(@Nullable String types)
        throws ReportRunnerQueryRestException {
        Set<ReportRunnerReportType> result = Sets.newHashSet();
        for (String type : Strings.nullToEmpty(types).split(",")) {
            if (Strings.isNullOrEmpty(type)) {
                continue;
            }
            try {
                result.add(ReportRunnerReportType.valueOf(type.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_SLOT_TYPE)
                    .addParameter("type", type)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private Set<ReportResult.Status> parseSlotStatuses(@Nullable String statuses)
        throws ReportRunnerQueryRestException {
        Set<ReportResult.Status> result = Sets.newHashSet();
        for (String status : Strings.nullToEmpty(statuses).split(",")) {
            if (Strings.isNullOrEmpty(status)) {
                continue;
            }
            try {
                result.add(ReportResult.Status.valueOf(status.trim()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_STATUS)
                    .addParameter("status", status)
                    .withCause(e).build();
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T extends ReportRunnerResponse> T toReportRunnerResponse(Authorization authorization,
        ReportRunner reportRunner, ZoneId timezone) {
        ReportRunnerResponseMapper responseMapper =
            reportRunnerResponseMappersRepository.getReportRunnerResponseMapper(reportRunner.getType());
        return (T) responseMapper.toReportRunner(authorization, reportRunner, timezone);
    }

    private ReportRunnerType parseReportRunnerType(String reportRunnerType) throws ReportRunnerQueryRestException {
        try {
            return ReportRunnerType.valueOf(reportRunnerType);
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_TYPE)
                .addParameter("type", reportRunnerType)
                .withCause(e).build();
        }
    }

    private Set<PauseStatus> parsePauseStatuses(String pauseStatuses) throws ReportRunnerQueryRestException {
        Set<PauseStatus> result = Sets.newHashSet();
        for (String pauseStatus : Strings.nullToEmpty(pauseStatuses).split(",")) {
            if (Strings.isNullOrEmpty(pauseStatus)) {
                continue;
            }
            try {
                result.add(PauseStatus.valueOf(pauseStatus.trim()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_PAUSE_STATUS)
                    .addParameter("status", pauseStatus)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private Set<AggregationStatus> parseAggregationStatuses(String aggregationStatuses)
        throws ReportRunnerQueryRestException {
        Set<AggregationStatus> result = Sets.newHashSet();
        for (String aggregationStatus : Strings.nullToEmpty(aggregationStatuses).split(",")) {
            if (Strings.isNullOrEmpty(aggregationStatus)) {
                continue;
            }
            try {
                result.add(AggregationStatus.valueOf(aggregationStatus.trim()));
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerQueryRestException.class)
                    .withErrorCode(ReportRunnerQueryRestException.REPORT_RUNNER_INVALID_AGGREGATION_STATUS)
                    .addParameter("status", aggregationStatus)
                    .withCause(e).build();
            }
        }
        return result;
    }

    private List<ReportRunnerResponse> toReportRunnersResponse(Authorization authorization,
        List<ReportRunner> reportRunners, ZoneId timezone) {
        List<ReportRunnerResponse> responses = Lists.newArrayList();
        for (ReportRunner reportRunner : reportRunners) {
            responses.add(toReportRunnerResponse(authorization, reportRunner, timezone));
        }

        return responses;
    }

    private Response downloadReport(Optional<String> contentType, Optional<String> format,
        Optional<String> limit, Optional<String> offset, Optional<String> filename, Authorization authorization,
        Report report)
        throws ReportRestException, AuthorizationException, ReportNotFoundException, ReportContentNotFoundException,
        ReportContentFormatNotFoundException, QueryLimitsRestException, ReportDownloadRestException {
        Report.Format reportFormat = getFormat(format, contentType, report);
        ReportFormatInfo downloadInfo = reportService.getReportInfo(authorization, report.getId(), reportFormat);

        boolean paginate = limit.isPresent() || offset.isPresent();
        if (!downloadInfo.isPreviewAvailable() && paginate) {
            throw RestExceptionBuilder.newBuilder(ReportDownloadRestException.class)
                .withErrorCode(ReportDownloadRestException.REPORT_PREVIEW_NOT_AVAILABLE)
                .addParameter("report_id", report.getId())
                .addParameter("format", reportFormat.name())
                .build();
        }
        int limitValue = paginate ? parseLimit(limit.orElse(null), DEFAULT_LIMIT) : 0;
        int offsetValue = paginate ? parseOffset(offset.orElse(null), 0) : 0;
        StreamingOutput streamer =
            outputStream -> {
                try {
                    if (paginate) {
                        reportService.downloadReportSummary(authorization, report.getId(), reportFormat,
                            limitValue, offsetValue, outputStream);
                    } else {
                        reportService.downloadReport(authorization, report.getId(), reportFormat,
                            outputStream);
                    }
                } catch (AuthorizationException | ReportNotFoundException | ReportContentNotFoundException
                    | ReportContentFormatNotFoundException | ReportContentDownloadException e) {
                    throw new ReportRuntimeException(e);
                }
            };

        Response.ResponseBuilder responseBuilder = Response.ok(streamer, downloadInfo.getFormat().getMimeType());
        if (!paginate) {
            responseBuilder.header(HttpHeaders.CONTENT_LENGTH, Long.valueOf(downloadInfo.getContentLength()));
        }
        responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,
            String.format(REPORT_CONTENT_DISPOSITION_FORMATTER,
                filename
                    .orElse(String.format(FILENAME_FORMATTER, downloadInfo.getName().toLowerCase(), report.getId())),
                downloadInfo.getFormat().getExtension()));

        return responseBuilder.build();
    }

    private Report.Format getFormat(Optional<String> format, Optional<String> contentType, Report report)
        throws ReportRestException {
        if (format.isPresent() && !format.get().isEmpty()) {
            return getFormat(format.get().split("\\.")[1], report.getId());
        } else if (contentType.isPresent() && !contentType.get().isEmpty()) {
            try {
                return Report.Format.valueOfMimeType(contentType.get());
            } catch (IllegalArgumentException e) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_TYPE_NOT_SUPPORTED)
                    .addParameter("report_id", report.getId())
                    .addParameter("content_type", contentType.get())
                    .withCause(e)
                    .build();
            }
        } else {
            return report.getFormats().iterator().next();
        }
    }

    private Report.Format getFormat(String format, Id<Report> reportId) throws ReportRestException {
        try {
            return Report.Format.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_FORMAT_NOT_SUPPORTED)
                .addParameter("report_id", reportId)
                .addParameter("format", format)
                .withCause(e)
                .build();
        }
    }

    private interface Supplier {
        ReportRunner execute() throws AuthorizationException, ReportRunnerMissingNameException,
            ReportRunnerMissingParametersException, ReportRunnerInvalidParametersException,
            ReportRunnerReportTypeMissingException, ReportRunnerFormatNotSupportedException,
            ReportRunnerReportTypeNotFoundException, ReportRunnerInvalidScopesException,
            SftpDestinationNotFoundException, ReportRunnerValidationRestException, ReportRunnerNotFoundException,
            ReportRunnerMergeEmptyFormatException;
    }

    private ReportRunner upload(Supplier supplier)
        throws UserAuthorizationRestException, ReportRunnerValidationRestException, ReportRunnerNotFoundException {
        try {
            return supplier.execute();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_NAME).withCause(e)
                .build();
        } catch (ReportRunnerReportTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_REPORT_TYPE).withCause(e)
                .build();
        } catch (ReportRunnerMissingParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MISSING_PARAMETERS)
                .addParameter("parameters", e.getMissingParameters())
                .withCause(e).build();
        } catch (ReportRunnerInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_PARAMETER)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (ReportRunnerFormatNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_FORMATS).withCause(e)
                .addParameter("formats", e.getFormats())
                .build();
        } catch (ReportRunnerMergeEmptyFormatException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_MERGE_EMPTY_FORMATS).withCause(e)
                .build();
        } catch (ReportRunnerReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_TYPE_NOT_FOUND).withCause(e)
                .build();
        } catch (ReportRunnerInvalidScopesException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_SCOPES).withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_SFTP_SERVER)
                .addParameter("sftp_server_id", e.getSftpDestinationId())
                .withCause(e)
                .build();
        }
    }

    private final class CreateReportRunnerSupplier implements Supplier {
        private final ReportRunnerCreateRequest request;
        private final Authorization authorization;

        private CreateReportRunnerSupplier(ReportRunnerCreateRequest request, Authorization authorization) {
            this.request = request;
            this.authorization = authorization;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public ReportRunner execute()
            throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerMissingParametersException,
            ReportRunnerInvalidParametersException, ReportRunnerReportTypeMissingException,
            ReportRunnerFormatNotSupportedException, ReportRunnerReportTypeNotFoundException,
            ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
            ReportRunnerMergeEmptyFormatException {
            ReportRunnerUploader reportRunnerUploader =
                reportRunnerUploadersRepository
                    .getReportRunnerUploader(ReportRunnerType.valueOf(request.getType().name()));
            return reportRunnerUploader.upload(authorization, request);
        }
    }

    private final class UpdateReportRunnerSupplier implements Supplier {
        private final Authorization authorization;
        private final Id<ReportRunner> reportRunnerId;
        private final ReportRunnerUpdateRequest request;

        private UpdateReportRunnerSupplier(Authorization authorization, Id<ReportRunner> reportRunnerId,
            ReportRunnerUpdateRequest request) {
            this.authorization = authorization;
            this.reportRunnerId = reportRunnerId;
            this.request = request;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public ReportRunner execute()
            throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerMissingParametersException,
            ReportRunnerInvalidParametersException, ReportRunnerReportTypeMissingException,
            ReportRunnerFormatNotSupportedException, ReportRunnerReportTypeNotFoundException,
            ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
            ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {
            ReportRunnerType runnerType = reportRunnerService.getById(authorization, reportRunnerId).getType();
            if (!runnerType.equals(ReportRunnerType.valueOf(request.getType().name()))) {
                throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                    .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_WRONG_TYPE)
                    .build();
            }
            ReportRunnerUploader reportRunnerUploader =
                reportRunnerUploadersRepository.getReportRunnerUploader(runnerType);
            return reportRunnerUploader.upload(authorization, reportRunnerId, request);
        }
    }

    private final class DuplicateReportRunnerSupplier implements Supplier {
        private final Authorization authorization;
        private final Id<ReportRunner> reportRunnerId;
        private final boolean allowDuplicate;
        private final ReportRunnerUpdateRequest request;

        private DuplicateReportRunnerSupplier(Authorization authorization, Id<ReportRunner> reportRunnerId,
            boolean allowDuplicate, ReportRunnerUpdateRequest request) {
            this.authorization = authorization;
            this.reportRunnerId = reportRunnerId;
            this.allowDuplicate = allowDuplicate;
            this.request = request;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public ReportRunner execute()
            throws AuthorizationException, ReportRunnerMissingNameException, ReportRunnerMissingParametersException,
            ReportRunnerInvalidParametersException, ReportRunnerReportTypeMissingException,
            ReportRunnerFormatNotSupportedException, ReportRunnerReportTypeNotFoundException,
            ReportRunnerInvalidScopesException, SftpDestinationNotFoundException, ReportRunnerValidationRestException,
            ReportRunnerNotFoundException, ReportRunnerMergeEmptyFormatException {
            ReportRunnerUploader reportRunnerUploader = reportRunnerUploadersRepository.getReportRunnerUploader(
                reportRunnerService.getTemplateReportRunnerById(authorization, reportRunnerId).getType());
            return reportRunnerUploader.duplicate(authorization, reportRunnerId, allowDuplicate, request);
        }
    }

    private ZoneId getClientTimezone(Id<ClientHandle> clientId) {
        try {
            return clientService.getPublicClientById(clientId).getTimeZone();
        } catch (ClientNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private List<String> parseTagsToList(String havingAllTags) {
        return Arrays.stream(havingAllTags.split(",")).collect(Collectors.toList());
    }
}
