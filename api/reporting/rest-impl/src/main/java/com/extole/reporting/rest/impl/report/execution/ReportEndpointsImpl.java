package com.extole.reporting.rest.impl.report.execution;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.common.lang.date.DateTimeBuilderValidationException;
import com.extole.common.lang.date.DateTimeRange;
import com.extole.common.lang.date.DateTimeRangeBuilder;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.entity.report.ReportResult;
import com.extole.reporting.entity.report.ReportType.Scope;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.report.ReportOrderBy;
import com.extole.reporting.rest.report.ReportOrderDirection;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.CreateReportRequest;
import com.extole.reporting.rest.report.execution.FormatReportInfoResponse;
import com.extole.reporting.rest.report.execution.LatestReportDownloadRequest;
import com.extole.reporting.rest.report.execution.LatestReportRequest;
import com.extole.reporting.rest.report.execution.ReportDebugResponse;
import com.extole.reporting.rest.report.execution.ReportDownloadRestException;
import com.extole.reporting.rest.report.execution.ReportEndpoints;
import com.extole.reporting.rest.report.execution.ReportInfoResponse;
import com.extole.reporting.rest.report.execution.ReportListQueryRestException;
import com.extole.reporting.rest.report.execution.ReportListRequest;
import com.extole.reporting.rest.report.execution.ReportNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportRestException;
import com.extole.reporting.rest.report.execution.ReportStatus;
import com.extole.reporting.rest.report.execution.ReportTypeNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;
import com.extole.reporting.rest.report.execution.UpdateReportRequest;
import com.extole.reporting.service.ReportInvalidParametersException;
import com.extole.reporting.service.ReportInvalidScopesException;
import com.extole.reporting.service.ReportInvalidStateException;
import com.extole.reporting.service.ReportMissingParametersException;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.ReportTypeNotFoundException;
import com.extole.reporting.service.report.LatestReportQueryBuilder;
import com.extole.reporting.service.report.ReportBuilder;
import com.extole.reporting.service.report.ReportContentDownloadException;
import com.extole.reporting.service.report.ReportContentFormatNotFoundException;
import com.extole.reporting.service.report.ReportContentNotFoundException;
import com.extole.reporting.service.report.ReportDebug;
import com.extole.reporting.service.report.ReportDisplayNameEmptyException;
import com.extole.reporting.service.report.ReportDisplayNameInvalidException;
import com.extole.reporting.service.report.ReportDisplayNameTooLongException;
import com.extole.reporting.service.report.ReportEditBuilder;
import com.extole.reporting.service.report.ReportFormatInfo;
import com.extole.reporting.service.report.ReportFormatNotSupportedException;
import com.extole.reporting.service.report.ReportListQueryBuilder;
import com.extole.reporting.service.report.ReportMissingFilterException;
import com.extole.reporting.service.report.ReportService;
import com.extole.reporting.service.report.TagsTooLongException;
import com.extole.reporting.service.report.type.ReportTypeNameMissingException;

@Provider
public class ReportEndpointsImpl implements ReportEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ReportEndpointsImpl.class);

    private static final String REPORT_CONTENT_DISPOSITION_FORMATTER = "attachment; filename = %s.%s";
    private static final String FILENAME_FORMATTER = "report-%s-%s";

    private static final int DEFAULT_LIMIT = 100;
    private static final Duration REPORT_INTEGRATION_TOKEN_THRESHOLD = Duration.ofDays(10);

    private final ClientAuthorizationProvider clientAuthorizationProvider;
    private final CombinedAuthorizationProvider combinedAuthorizationProvider;
    private final ReportService reportService;
    private final ReportResponseMapper reportResponseMapper;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ReportEndpointsImpl(ClientAuthorizationProvider clientAuthorizationProvider,
        CombinedAuthorizationProvider combinedAuthorizationProvider,
        ReportService reportService,
        ReportResponseMapper reportResponseMapper,
        @Context HttpHeaders requestHeaders) {
        this.clientAuthorizationProvider = clientAuthorizationProvider;
        this.combinedAuthorizationProvider = combinedAuthorizationProvider;
        this.reportService = reportService;
        this.reportResponseMapper = reportResponseMapper;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public ReportResponse createReport(String accessToken, CreateReportRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportValidationRestException, ReportTypeNotFoundRestException {
        ClientAuthorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);

        LOG.info("Creating report {} for client {} and token {}", request.getReportType(), authorization.getClientId(),
            accessToken);
        try {
            ReportBuilder builder = reportService.createReport(authorization, request.getReportType());
            if (request.getFormat() != null) {
                builder.withFormats(Collections.singleton(Report.Format.valueOf(request.getFormat().name())));
            }
            if (request.getFormats() != null && !request.getFormats().isEmpty()) {
                builder.withFormats(
                    request.getFormats().stream().map(reportFormat -> Report.Format.valueOf(reportFormat.name()))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
            builder.withParameters(request.getParameters());
            if (request.getDisplayName() != null) {
                builder.withDisplayName(request.getDisplayName());
            }
            if (request.getTags() != null) {
                builder.withTags(request.getTags());
            }
            String cleanSftpReportName = StringUtils.trimToNull(request.getSftpReportName());
            if (cleanSftpReportName != null) {
                builder.withSftpReportName(cleanSftpReportName);
            }
            if (request.getScopes() != null) {
                builder.withScopes(request.getScopes().stream()
                    .map(ReportTypeScope::name)
                    .map(Scope::valueOf)
                    .collect(Collectors.toSet()));
            }
            if (!request.rerun().booleanValue()) {
                // TODO ENG-9841 remove rerun support
                LOG.warn("Legacy path. Requesting creation of a report without rerun. Request {}", request);
            }
            if (!Strings.isNullOrEmpty(request.getSftpServerId())) {
                builder.withSftpServerId(Id.valueOf(request.getSftpServerId()));
            }
            Report report = builder.withRerun(request.rerun().booleanValue()).execute();
            // TODO ENG-13418 remove after checking if anyone integrated
            if (Duration.between(Instant.now(), authorization.getExpiresAt())
                .compareTo(REPORT_INTEGRATION_TOKEN_THRESHOLD) > 0) {
                LOG.warn(
                    "Potential integration: report created for client {}, token {}, report id {}, executor type {}",
                    authorization.getClientId(), accessToken, report.getId(), report.getExecutorType().name());
            }
            return reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeNotFoundRestException.class)
                .withErrorCode(ReportTypeNotFoundRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("report_type", request.getReportType())
                .withCause(e).build();
        } catch (ReportTypeNameMissingException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.MISSING_TYPE)
                .withCause(e).build();
        } catch (ReportDisplayNameEmptyException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_EMPTY)
                .withCause(e).build();
        } catch (ReportDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_TOO_LONG)
                .withCause(e).build();
        } catch (TagsTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.TAGS_TOO_LONG)
                .addParameter("tags", e.getTags())
                .withCause(e).build();
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
        } catch (ReportInvalidScopesException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_SCOPES)
                .withCause(e).build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_SFTP_SERVER)
                .addParameter("sftp_server_id", e.getSftpDestinationId())
                .withCause(e)
                .build();
        } catch (ReportDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .addParameter("display_name", request.getDisplayName())
                .withCause(e).build();
        }
    }

    @Override
    public ReportResponse retryReport(String accessToken, String reportId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException, ReportTypeNotFoundRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);

        LOG.info("Retrying report {} for client {} and token {}", reportId, authorization.getClientId(), accessToken);
        try {
            Report report = reportService.retryReport(authorization, Id.valueOf(reportId));
            return reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeNotFoundRestException.class)
                .withErrorCode(ReportTypeNotFoundRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("report_type", e.getReportTypeName())
                .withCause(e).build();
        }
    }

    @Override
    public ReportResponse updateReport(String accessToken, String reportId, UpdateReportRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException, ReportValidationRestException {

        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
        try {
            ReportEditBuilder builder = reportService.editReport(authorization, Id.valueOf(reportId));
            request.isVisible().ifPresent(builder::withVisible);
            if (request.getDisplayName().isPresent()) {
                builder.withDisplayName(request.getDisplayName().getValue());
            }
            request.getScopes().map(scopes -> scopes.stream()
                .map(reportTypeScope -> reportTypeScope.name())
                .map(Scope::valueOf)
                .collect(Collectors.toSet())).ifPresent(builder::withScopes);
            request.getTags().ifPresent(builder::withTags);
            return reportResponseMapper.toReportResponse(authorization, builder.save(), requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        } catch (ReportDisplayNameEmptyException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_EMPTY)
                .withCause(e).build();
        } catch (ReportDisplayNameTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_TOO_LONG)
                .withCause(e).build();
        } catch (TagsTooLongException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.TAGS_TOO_LONG)
                .addParameter("tags", e.getTags())
                .withCause(e).build();
        } catch (ReportDisplayNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.DISPLAY_NAME_ILLEGAL_CHARACTER)
                .addParameter("display_name", request.getDisplayName())
                .withCause(e).build();
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ReportInvalidScopesException) {
                throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                    .withErrorCode(ReportValidationRestException.REPORT_INVALID_SCOPES)
                    .withCause(e).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause)
                    .build();
            }
        }
    }

    @Override
    public ReportResponse readReport(String accessToken, String reportId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException {
        Authorization authorization = combinedAuthorizationProvider.getAuthorization(accessToken);
        Report report;
        try {
            report = reportService.getReportById(authorization, Id.valueOf(reportId));
            return reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        }
    }

    @Override
    public Response downloadReport(String accessToken, String contentType, String reportId,
        String format, String limit, String offset, String filename)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException,
        ReportNotFoundRestException, ReportDownloadRestException {
        Authorization authorization = combinedAuthorizationProvider.getAuthorization(accessToken);
        try {
            Report report = reportService.getPublicReportById(authorization, Id.valueOf(reportId));
            LOG.info("Downloading report. Executor {} type {} client {} id {}", report.getExecutorType(),
                report.getName(), report.getClientId(), report.getId());
            return downloadReport(Optional.ofNullable(contentType), Optional.ofNullable(format),
                Optional.ofNullable(limit), Optional.ofNullable(offset), Optional.ofNullable(filename), authorization,
                report);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e)
                .build();
        } catch (ReportContentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e)
                .build();
        } catch (ReportContentFormatNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .addParameter("format", contentType)
                .withCause(e)
                .build();
        } catch (ReportRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthorizationException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_ACCESS_DENIED)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                    .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                    .addParameter("report_id", reportId)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                    .addParameter("report_id", reportId)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentFormatNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND)
                    .addParameter("report_id", reportId)
                    .addParameter("format", contentType)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentDownloadException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_DOWNLOADED)
                    .addParameter("report_id", reportId)
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

    @Override
    public FormatReportInfoResponse getReportInfoByFormat(String accessToken, String reportIdValue, String format)
        throws UserAuthorizationRestException, ReportRestException, ReportNotFoundRestException {
        Authorization authorization = combinedAuthorizationProvider.getAuthorization(accessToken);
        Id<Report> reportId = Id.valueOf(reportIdValue);
        Report.Format reportFormat = getFormat(format, reportId);
        try {
            ReportFormatInfo reportFormatInfo = reportService.getReportInfo(authorization, reportId, reportFormat);
            return toReportInfoResponse(reportFormatInfo);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND).addParameter("report_id", reportIdValue)
                .withCause(e)
                .build();
        } catch (ReportContentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                .addParameter("report_id", reportIdValue)
                .withCause(e)
                .build();
        } catch (ReportContentFormatNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND)
                .addParameter("report_id", reportIdValue)
                .addParameter("format", format)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportInfoResponse getReportInfo(String accessToken, String reportIdValue)
        throws UserAuthorizationRestException, ReportRestException, ReportNotFoundRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
        Id<Report> reportId = Id.valueOf(reportIdValue);
        try {
            return new ReportInfoResponse(reportService.getReportInfo(authorization, reportId).longValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND).addParameter("report_id", reportIdValue)
                .withCause(e)
                .build();
        } catch (ReportContentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND)
                .addParameter("report_id", reportIdValue)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportResponse cancelReport(String accessToken, String reportId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException, ReportValidationRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
        Report report;
        try {
            report = reportService.cancelReport(authorization, Id.valueOf(reportId));
            return reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        } catch (ReportInvalidStateException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_INVALID_OPERATION)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        }
    }

    @Override
    public ReportResponse deleteReport(String accessToken, String reportId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
        Report report;
        try {
            report = reportService.deleteReport(authorization, Id.valueOf(reportId));
            return reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        }
    }

    @Override
    public List<ReportResponse> listReports(String accessToken, ReportListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportListQueryRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportListRequest.builder().build();
            }

            ReportListQueryBuilder listFilterBuilder = reportService.getReports(authorization);
            request.getReportTypeName().filter(StringUtils::isNotEmpty)
                .ifPresent(listFilterBuilder::withReportTypeName);
            request.getDisplayName().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withDisplayName);
            request.getStatuses().map(statuses -> statuses.stream().filter(status -> !Objects.isNull(status))
                .map(status -> ReportResult.Status.valueOf(status.name())).collect(Collectors.toSet()))
                .ifPresent(listFilterBuilder::withStatuses);
            listFilterBuilder.withUserIds(request.getUserIds().stream().filter(StringUtils::isNotEmpty).map(Id::valueOf)
                .collect(Collectors.toSet()));

            request.getHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(listFilterBuilder::withHavingAnyTags);
            request.getHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(listFilterBuilder::withHavingAllTags);
            request.getExcludeHavingAnyTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(listFilterBuilder::withExcludeHavingAnyTags);
            request.getExcludeHavingAllTags().filter(StringUtils::isNotEmpty)
                .map(tags -> parseTagsToList(tags))
                .ifPresent(listFilterBuilder::withExcludeHavingAllTags);
            request.getSearchQuery().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withSearchQuery);
            if (request.getCreationInterval().isPresent()) {
                listFilterBuilder.withCreationInterval(
                    parseTimeRange(request.getCreationInterval().get(), request.getTimezone().get()));
            }
            listFilterBuilder.withOffset(
                parseOffset(request.getOffset().orElse(BigDecimal.ZERO.toString()), BigDecimal.ZERO.intValue()));
            listFilterBuilder
                .withLimit(parseLimit(request.getLimit().orElse(String.valueOf(DEFAULT_LIMIT)), DEFAULT_LIMIT));
            request.getOrderBy().map(ReportOrderBy::name).map(com.extole.reporting.entity.report.ReportOrderBy::valueOf)
                .ifPresent(listFilterBuilder::withOrderBy);
            request.getOrder().map(ReportOrderDirection::name)
                .map(com.extole.reporting.entity.report.ReportOrderDirection::valueOf)
                .ifPresent(listFilterBuilder::withOrder);

            List<Report> reports = listFilterBuilder.execute();
            List<ReportResponse> reportResponses = new ArrayList<>();
            for (Report report : reports) {
                reportResponses.add(reportResponseMapper.toReportResponse(authorization, report, requestHeaders,
                    request.getTimezone().orElse(null)));
            }
            return reportResponses;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public ReportResponse getLatestReport(String accessToken, LatestReportRequest request)
        throws UserAuthorizationRestException, ReportRestException {
        Authorization authorization = combinedAuthorizationProvider.getAuthorization(accessToken);
        if (request == null) {
            request = LatestReportRequest.builder().build();
        }
        try {
            LatestReportQueryBuilder queryBuilder = reportService.getLatestReportByTags(authorization)
                .withStatuses(Sets.newHashSet(ReportResult.Status.DONE, ReportResult.Status.SFTP_DELIVERY_FAILED));

            request.getHavingAnyTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withHavingAnyTags);

            request.getHavingAllTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withHavingAllTags);

            request.getExcludeHavingAnyTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withExcludeHavingAnyTags);

            request.getExcludeHavingAllTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withExcludeHavingAllTags);

            request.getOrderBy()
                .map(value -> com.extole.reporting.entity.report.ReportOrderBy.valueOf(value.name()))
                .ifPresent(queryBuilder::withOrderBy);

            request.getOrder()
                .map(value -> com.extole.reporting.entity.report.ReportOrderDirection.valueOf(value.name()))
                .ifPresent(queryBuilder::withOrder);

            Optional<Report> report = queryBuilder.execute();

            if (report.isPresent()) {
                return reportResponseMapper.toReportResponse(authorization, report.get(), requestHeaders,
                    request.getTimezone().orElse(null));
            } else {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.LATEST_REPORT_NOT_FOUND)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportMissingFilterException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_FILTER_MISSING).withCause(e).build();
        }
    }

    @Override
    public Response downloadLatestReport(String accessToken, String contentType, String format,
        LatestReportDownloadRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException, ReportRestException,
        ReportNotFoundRestException, ReportDownloadRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);
        try {
            if (request == null) {
                request = LatestReportDownloadRequest.builder().build();
            }
            LatestReportQueryBuilder queryBuilder = reportService.getLatestReportByTags(authorization)
                .withStatuses(ImmutableSet.of(ReportResult.Status.DONE, ReportResult.Status.SFTP_DELIVERY_FAILED));

            request.getHavingAnyTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withHavingAnyTags);

            request.getHavingAllTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withHavingAllTags);

            request.getExcludeHavingAnyTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withExcludeHavingAnyTags);

            request.getExcludeHavingAllTags()
                .filter(value -> !Strings.isNullOrEmpty(value))
                .map(value -> parseTagsToList(value))
                .ifPresent(queryBuilder::withExcludeHavingAllTags);

            request.getOrderBy()
                .map(value -> com.extole.reporting.entity.report.ReportOrderBy.valueOf(value.name()))
                .ifPresent(queryBuilder::withOrderBy);

            request.getOrder()
                .map(value -> com.extole.reporting.entity.report.ReportOrderDirection.valueOf(value.name()))
                .ifPresent(queryBuilder::withOrder);

            Optional<Report> report = queryBuilder.execute();

            if (report.isEmpty()) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.LATEST_REPORT_NOT_FOUND)
                    .build();
            }
            return downloadReport(Optional.ofNullable(contentType), Optional.ofNullable(format),
                request.getLimit(), request.getOffset(), request.getFilename(), authorization,
                report.get());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND_FOR_SOURCE_AND_TAGS)
                .addParameter("having_any_tags", request.getHavingAnyTags())
                .addParameter("having_all_tags", request.getHavingAllTags())
                .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                .withCause(e)
                .build();
        } catch (ReportContentNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_FOUND_FOR_TAGS)
                .addParameter("having_any_tags", request.getHavingAnyTags())
                .addParameter("having_all_tags", request.getHavingAllTags())
                .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                .withCause(e)
                .build();
        } catch (ReportContentFormatNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND_FOR_TAGS)
                .addParameter("having_any_tags", request.getHavingAnyTags())
                .addParameter("having_all_tags", request.getHavingAllTags())
                .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                .addParameter("format", contentType)
                .withCause(e)
                .build();
        } catch (ReportMissingFilterException e) {
            throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                .withErrorCode(ReportRestException.REPORT_FILTER_MISSING).withCause(e).build();
        } catch (ReportRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthorizationException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_ACCESS_DENIED)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                    .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND_FOR_SOURCE_AND_TAGS)
                    .addParameter("having_any_tags", request.getHavingAnyTags())
                    .addParameter("having_all_tags", request.getHavingAllTags())
                    .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                    .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND_FOR_TAGS)
                    .addParameter("having_any_tags", request.getHavingAnyTags())
                    .addParameter("having_all_tags", request.getHavingAllTags())
                    .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                    .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentFormatNotFoundException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_FORMAT_NOT_FOUND_FOR_TAGS)
                    .addParameter("having_any_tags", request.getHavingAnyTags())
                    .addParameter("having_all_tags", request.getHavingAllTags())
                    .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                    .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
                    .addParameter("format", contentType)
                    .withCause(cause)
                    .build();
            } else if (cause instanceof ReportContentDownloadException) {
                throw RestExceptionBuilder.newBuilder(ReportRestException.class)
                    .withErrorCode(ReportRestException.REPORT_CONTENT_NOT_DOWNLOADED_FOR_TAGS)
                    .addParameter("having_any_tags", request.getHavingAnyTags())
                    .addParameter("having_all_tags", request.getHavingAllTags())
                    .addParameter("exclude_having_any_tags", request.getExcludeHavingAnyTags())
                    .addParameter("exclude_having_all_tags", request.getExcludeHavingAllTags())
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

    @Override
    public ReportDebugResponse readReportDebug(String accessToken, String reportId)
        throws UserAuthorizationRestException, ReportNotFoundRestException {
        Authorization authorization = clientAuthorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportDebug report = reportService.getReportDebug(authorization, Id.valueOf(reportId));
            ReportDebugResponse.Builder builder = ReportDebugResponse.builder()
                .withReportId(report.getReportId().getValue())
                .withReportStatus(ReportStatus.valueOf(report.getStatus().name()));
            report.getErrorCode().ifPresent(builder::withErrorCode);
            report.getErrorMessage().ifPresent(builder::withErrorMessage);
            report.getDebugMessage().ifPresent(builder::withDebugMessage);
            return builder.build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportNotFoundRestException.class)
                .withErrorCode(ReportNotFoundRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", reportId)
                .withCause(e).build();
        }
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

    private DateTimeRange parseTimeRange(String timerange, ZoneId timezone) throws ReportListQueryRestException {
        try {
            return new DateTimeRangeBuilder()
                .withDefaultTimezone(timezone)
                .withTimeParsingEnabled(true)
                .withRangeString(timerange)
                .build();
        } catch (DateTimeBuilderValidationException e) {
            throw RestExceptionBuilder.newBuilder(ReportListQueryRestException.class)
                .withErrorCode(ReportListQueryRestException.INVALID_TIMERANGE_FORMAT)
                .withCause(e)
                .build();
        }
    }

    private FormatReportInfoResponse toReportInfoResponse(ReportFormatInfo report) {
        return new FormatReportInfoResponse(report.getContentLength(), report.getTotalRows());
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

    private List<String> parseTagsToList(String havingAllTags) {
        return Arrays.stream(havingAllTags.split(",")).collect(Collectors.toList());
    }
}
