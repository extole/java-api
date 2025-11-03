package com.extole.reporting.rest.impl.report.schedule;

import static com.extole.common.rest.support.parser.QueryLimitsParser.parseLimit;
import static com.extole.common.rest.support.parser.QueryLimitsParser.parseOffset;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.report.runner.schedule.ReportSchedule;
import com.extole.model.entity.report.runner.schedule.ReportScheduleOrder;
import com.extole.model.entity.report.runner.schedule.ScheduleFrequency;
import com.extole.model.entity.report.type.Format;
import com.extole.model.entity.report.type.ReportInvalidParametersException;
import com.extole.model.entity.report.type.ReportParameter;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.service.client.sftp.SftpDestinationNotFoundException;
import com.extole.model.service.report.ReportMissingParametersException;
import com.extole.model.service.report.runner.ReportRunnerDuplicateException;
import com.extole.model.service.report.runner.ReportRunnerInvalidSortByException;
import com.extole.model.service.report.runner.ReportRunnerNameInvalidException;
import com.extole.model.service.report.runner.ReportScheduleListQueryBuilder;
import com.extole.model.service.report.runner.ReportingServiceException;
import com.extole.model.service.report.runner.schedule.ReportScheduleBuilder;
import com.extole.model.service.report.runner.schedule.ReportScheduleFormatNotSupportedException;
import com.extole.model.service.report.runner.schedule.ReportScheduleFrequencyNotSupportedForLegacySftpException;
import com.extole.model.service.report.runner.schedule.ReportScheduleInvalidParametersException;
import com.extole.model.service.report.runner.schedule.ReportScheduleInvalidScopesException;
import com.extole.model.service.report.runner.schedule.ReportScheduleMergeEmptyFormatSupportedException;
import com.extole.model.service.report.runner.schedule.ReportScheduleMissingFrequencyException;
import com.extole.model.service.report.runner.schedule.ReportScheduleMissingNameException;
import com.extole.model.service.report.runner.schedule.ReportScheduleMissingParametersException;
import com.extole.model.service.report.runner.schedule.ReportScheduleMissingScheduleStartDateException;
import com.extole.model.service.report.runner.schedule.ReportScheduleNotFoundException;
import com.extole.model.service.report.runner.schedule.ReportScheduleReportTypeMissingException;
import com.extole.model.service.report.runner.schedule.ReportScheduleReportTypeNotFoundException;
import com.extole.model.service.report.runner.schedule.ReportScheduleService;
import com.extole.model.service.report.runner.schedule.ReportScheduleSftpNotSupportedException;
import com.extole.model.service.report.runner.schedule.ReportScheduleUpdateManagedByGitException;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.rest.impl.report.ReportInvalidParametersRestExceptionMapper;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;
import com.extole.reporting.rest.report.schedule.CreateReportScheduleRequest;
import com.extole.reporting.rest.report.schedule.ReportScheduleEndpoints;
import com.extole.reporting.rest.report.schedule.ReportScheduleListRequest;
import com.extole.reporting.rest.report.schedule.ReportScheduleResponse;
import com.extole.reporting.rest.report.schedule.ReportScheduleRestException;
import com.extole.reporting.rest.report.schedule.ReportScheduleValidationRestException;
import com.extole.reporting.rest.report.schedule.UpdateReportScheduleRequest;
import com.extole.reporting.service.report.ReportFormatNotSupportedException;
import com.extole.reporting.service.report.ReportSftpNotSupportedException;
import com.extole.reporting.service.report.runner.NoExecutionTimeRangesException;
import com.extole.reporting.service.report.runner.ReportRunnerPausedException;
import com.extole.reporting.service.report.runner.ReportRunnerSlotNotSupportedException;
import com.extole.reporting.service.report.schedule.ReportScheduleExecutionService;

@Provider
public class ReportScheduleEndpointsImpl implements ReportScheduleEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(ReportScheduleEndpointsImpl.class);
    private static final String LEGACY_TIME_ZONE_FORMAT_PARAMETER = "legacy_timezone_format";

    private static final int DEFAULT_LIMIT = 100;

    private final ClientAuthorizationProvider authorizationProvider;
    private final ReportScheduleService reportScheduleService;
    private final ReportScheduleExecutionService reportScheduleExecutionService;
    private final ReportResponseMapper reportResponseMapper;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ReportScheduleEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ReportScheduleService reportScheduleService,
        ReportScheduleExecutionService reportScheduleExecutionService,
        ReportResponseMapper reportResponseMapper,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.reportScheduleService = reportScheduleService;
        this.reportScheduleExecutionService = reportScheduleExecutionService;
        this.reportResponseMapper = reportResponseMapper;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public ReportScheduleResponse createReportSchedule(String accessToken, CreateReportScheduleRequest request,
        ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        LOG.info("Creating report schedule {} for client {} and token {}", request.getName(),
            authorization.getClientId(), accessToken);

        ReportSchedule reportSchedule = handleExceptions(() -> {
            ReportScheduleBuilder builder = reportScheduleService.createReportSchedule(authorization);

            builder.withName(request.getName())
                .withReportType(request.getReportType())
                .withScheduleStartDate(request.getScheduleStartDate().toInstant());

            if (request.getFrequency() != null) {
                builder.withFrequency(ScheduleFrequency.valueOf(request.getFrequency().name()));
            }
            if (request.getFormats() != null && !request.getFormats().isEmpty()) {
                builder.withFormats(
                    request.getFormats().stream()
                        .map(reportFormat -> Format.valueOf(reportFormat.name()))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
            }

            builder.withParameters(request.getParameters());

            if (request.isLegacySftpReportNameFormat() != null) {
                builder.withLegacySftpReportNameFormat(request.isLegacySftpReportNameFormat().booleanValue());
            }
            if (request.getTags() != null) {
                builder.withTags(request.getTags());
            }
            if (request.getScopes() != null) {
                Set<ReportType.Scope> scopes = request.getScopes().stream()
                    .map(ReportTypeScope::name)
                    .map(ReportType.Scope::valueOf)
                    .collect(Collectors.toSet());
                builder.withScopes(scopes);
            }

            if (!Strings.isNullOrEmpty(request.getSftpServerId())) {
                builder.withSftpServerId(Id.valueOf(request.getSftpServerId()));
            }

            return builder.save();
        });
        return toReportScheduleResponse(authorization, reportSchedule, timeZone);
    }

    @Override
    public ReportScheduleResponse updateReportSchedule(String accessToken, String reportScheduleId,
        UpdateReportScheduleRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        ReportSchedule reportSchedule = handleExceptions(() -> {
            try {
                ReportScheduleBuilder builder =
                    reportScheduleService.updateReportSchedule(authorization, Id.valueOf(reportScheduleId));

                if (request.getName() != null) {
                    builder.withName(request.getName());
                }
                if (request.getFrequency() != null) {
                    builder.withFrequency(ScheduleFrequency.valueOf(request.getFrequency().name()));
                }
                if (request.getReportType() != null) {
                    builder.withReportType(request.getReportType());
                }
                if (request.getScheduleStartDate().isPresent()) {
                    builder.withScheduleStartDate(request.getScheduleStartDate().get().toInstant());
                }
                if (request.getFormats() != null && !request.getFormats().isEmpty()) {
                    builder.withFormats(
                        request.getFormats().stream()
                            .map(reportFormat -> Format.valueOf(reportFormat.name()))
                            .collect(Collectors.toCollection(LinkedHashSet::new)));
                }
                if (request.getParameters() != null) {
                    builder.withParameters(request.getParameters());
                }
                if (request.isLegacySftpReportNameFormat() != null) {
                    builder.withLegacySftpReportNameFormat(request.isLegacySftpReportNameFormat().booleanValue());
                }
                if (request.getScopes() != null) {
                    Set<ReportType.Scope> scopes = request.getScopes().stream()
                        .map(ReportTypeScope::name)
                        .map(ReportType.Scope::valueOf)
                        .collect(Collectors.toSet());
                    builder.withScopes(scopes);
                }
                if (request.getTags() != null) {
                    builder.withTags(request.getTags());
                }
                if (request.getSftpServerId() != null) {
                    if (request.getSftpServerId().isEmpty()) {
                        builder.withSftpServerId(null);
                    } else {
                        builder.withSftpServerId(Id.valueOf(request.getSftpServerId()));
                    }
                }
                return builder.save();
            } catch (ReportScheduleNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(ReportScheduleRestException.class)
                    .withErrorCode(ReportScheduleRestException.REPORT_SCHEDULE_NOT_FOUND)
                    .addParameter("report_schedule_id", e.getReportScheduleId().getValue())
                    .withCause(e).build();
            }
        });
        return toReportScheduleResponse(authorization, reportSchedule, timeZone);
    }

    @Override
    public ReportScheduleResponse readReportSchedule(String accessToken, String reportScheduleId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        ReportSchedule reportSchedule;
        try {
            reportSchedule = reportScheduleService.getReportScheduleById(authorization, Id.valueOf(reportScheduleId));
            return toReportScheduleResponse(authorization, reportSchedule, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportScheduleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleRestException.class)
                .withErrorCode(ReportScheduleRestException.REPORT_SCHEDULE_NOT_FOUND)
                .addParameter("report_schedule_id", e.getReportScheduleId().getValue())
                .withCause(e).build();
        }
    }

    @Override
    public ReportScheduleResponse deleteReportSchedule(String accessToken, String reportScheduleId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            ReportSchedule reportSchedule =
                reportScheduleService.deleteReportSchedule(authorization, Id.valueOf(reportScheduleId));
            return toReportScheduleResponse(authorization, reportSchedule, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportScheduleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleRestException.class)
                .withErrorCode(ReportScheduleRestException.REPORT_SCHEDULE_NOT_FOUND)
                .addParameter("report_schedule_id", reportScheduleId)
                .withCause(e).build();
        } catch (ReportScheduleUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_LOCKED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<ReportResponse> scheduleMissingReports(String accessToken, String reportScheduleId,
        Integer missingReportsToGenerate, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Report> reports =
                reportScheduleExecutionService.scheduleMissingReports(authorization, Id.valueOf(reportScheduleId),
                    missingReportsToGenerate != null ? missingReportsToGenerate.intValue() : 1);
            return reports.stream()
                .map(report -> reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportScheduleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleRestException.class)
                .withErrorCode(ReportScheduleRestException.REPORT_SCHEDULE_NOT_FOUND)
                .addParameter("report_schedule_id", reportScheduleId)
                .withCause(e).build();
        } catch (ReportScheduleInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_PARAMETER)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (ReportingServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public ReportResponse run(String accessToken, String reportScheduleId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return reportResponseMapper.toReportResponse(authorization,
                reportScheduleExecutionService.executeReport(authorization,
                    Id.valueOf(reportScheduleId), true),
                requestHeaders, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportMissingParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_MISSING_PARAMETERS)
                .addParameter("parameters", e.getMissingParameters())
                .withCause(e).build();
        } catch (ReportInvalidParametersException e) {
            throw ReportInvalidParametersRestExceptionMapper.getInstance().map(e);
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
        } catch (ReportScheduleNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleRestException.class)
                .withErrorCode(ReportScheduleRestException.REPORT_SCHEDULE_NOT_FOUND)
                .addParameter("report_schedule_id", reportScheduleId)
                .withCause(e).build();
        } catch (ClientNotFoundException | ReportingServiceException | NoExecutionTimeRangesException
            | ReportRunnerPausedException | ReportRunnerSlotNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public List<ReportScheduleResponse> listReportSchedules(String accessToken, ReportScheduleListRequest request)
        throws UserAuthorizationRestException, QueryLimitsRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            if (request == null) {
                request = ReportScheduleListRequest.builder().build();
            }
            ReportScheduleListQueryBuilder listFilterBuilder = reportScheduleService.getReportSchedules(authorization)
                .withOrderBy(ReportScheduleOrder.Field.CREATED_DATE)
                .withOrder(ReportScheduleOrder.Direction.DESCENDING);

            request.getReportType().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withReportType);
            request.getDisplayName().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withDisplayName);
            request.getUserId().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withUserId);

            request.getTags().filter(StringUtils::isNotEmpty)
                .map(tags -> Arrays.stream(tags.split(",")).distinct().collect(Collectors.toList()))
                .ifPresent(listFilterBuilder::withHavingAnyTags);
            request.getRequiredTags().filter(StringUtils::isNotEmpty)
                .map(tags -> Arrays.stream(tags.split(",")).distinct().collect(Collectors.toList()))
                .ifPresent(listFilterBuilder::withHavingAllTags);
            request.getExcludeTags().filter(StringUtils::isNotEmpty)
                .map(tags -> Arrays.stream(tags.split(",")).distinct().collect(Collectors.toList()))
                .ifPresent(listFilterBuilder::withExcludeHavingAnyTags);
            request.getSearchQuery().filter(StringUtils::isNotEmpty).ifPresent(listFilterBuilder::withSearchQuery);
            request.getOrder().filter(StringUtils::isNotEmpty).map(ReportScheduleOrder.Direction::valueOf)
                .ifPresent(listFilterBuilder::withOrder);
            request.getOrderBy().filter(StringUtils::isNotEmpty).map(ReportScheduleOrder.Field::valueOf)
                .ifPresent(listFilterBuilder::withOrderBy);

            listFilterBuilder
                .withOffset(
                    parseOffset(request.getOffset().orElse(BigDecimal.ZERO.toString()), BigDecimal.ZERO.intValue()))
                .withLimit(parseLimit(request.getLimit().orElse(String.valueOf(DEFAULT_LIMIT)), DEFAULT_LIMIT));
            return toReportSchedulesResponse(authorization, listFilterBuilder.execute(),
                request.getTimezone().orElse(null));
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    private List<ReportScheduleResponse> toReportSchedulesResponse(Authorization authorization,
        List<ReportSchedule> reportSchedules, ZoneId timeZone) {
        List<ReportScheduleResponse> responses = Lists.newArrayList();
        for (ReportSchedule reportSchedule : reportSchedules) {
            responses.add(toReportScheduleResponse(authorization, reportSchedule, timeZone));
        }

        return responses;
    }

    private ReportScheduleResponse toReportScheduleResponse(Authorization authorization, ReportSchedule reportSchedule,
        ZoneId timeZone) {
        ReportScheduleResponse.Builder builder = ReportScheduleResponse.builder()
            .withReportScheduleId(reportSchedule.getId().getValue())
            .withName(reportSchedule.getName())
            .withReportType(reportSchedule.getReportType())
            .withFrequency(com.extole.reporting.rest.report.schedule.ScheduleFrequency
                .valueOf(reportSchedule.getFrequency().name()))
            .withScheduleStartDate(reportSchedule.getScheduleStartDate().atZone(timeZone))
            .withFormats(reportSchedule.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                .collect(Collectors.toList()))
            .withCreatedDate(reportSchedule.getCreatedAt().atZone(timeZone))
            .withUpdatedDate(reportSchedule.getUpdatedAt().atZone(timeZone))
            .withVersion(reportSchedule.getVersion())
            .withParameters(toReportParametersResponse(reportSchedule))
            .withLegacySftpReportNameFormat(reportSchedule.isLegacySftpReportNameFormat())
            .withScopes(toReportScopes(authorization, reportSchedule))
            .withTags(reportSchedule.getTags())
            .withUserId(reportSchedule.getUserId().getValue());

        reportSchedule.getSftpServerId().map(id -> id.toString()).ifPresent(id -> builder.withSftpServerId(id));

        return builder.build();
    }

    private static Map<String, ReportParameterResponse> toReportParametersResponse(ReportSchedule reportSchedule) {
        return reportSchedule.getParameters().stream()
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(LEGACY_TIME_ZONE_FORMAT_PARAMETER))
            .collect(Collectors.toMap(parameter -> parameter.getDetails().getName(),
                ReportScheduleEndpointsImpl::toReportParameterResponse));
    }

    private static ReportParameterResponse toReportParameterResponse(ReportParameter parameter) {
        return new ReportParameterResponse(parameter.getValue(), new ReportParameterDetailsResponse(
            parameter.getDetails().getName(),
            parameter.getDetails().getDisplayName(),
            parameter.getDetails().getCategory().orElse(null),
            new ReportParameterTypeResponse(
                ReportParameterTypeName.valueOf(parameter.getDetails().getType().getName().name()),
                ParameterValueType.valueOf(parameter.getDetails().getType().getValueType().name()),
                parameter.getDetails().getType().getValues()),
            parameter.getDetails().isRequired(),
            parameter.getDetails().getOrder()));
    }

    private Set<ReportTypeScope> toReportScopes(Authorization authorization, ReportSchedule reportSchedule) {
        Predicate<ReportType.Scope> scopeFilter = scope -> true;
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_SUPERUSER));
        }
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_ADMIN)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_ADMIN));
        }

        return reportSchedule.getScopes().stream().filter(scopeFilter).map(ReportType.Scope::name)
            .map(ReportTypeScope::valueOf)
            .collect(Collectors.toSet());
    }

    private interface Supplier {
        ReportSchedule execute() throws AuthorizationException, ReportScheduleMissingNameException,
            ReportScheduleMissingScheduleStartDateException, ReportScheduleMissingFrequencyException,
            ReportScheduleMissingParametersException, ReportScheduleInvalidParametersException,
            ReportScheduleReportTypeMissingException, ReportScheduleRestException,
            ReportScheduleFormatNotSupportedException, ReportScheduleReportTypeNotFoundException,
            ReportScheduleFrequencyNotSupportedForLegacySftpException, ReportScheduleInvalidScopesException,
            ReportScheduleSftpNotSupportedException, SftpDestinationNotFoundException,
            ReportScheduleUpdateManagedByGitException, ReportRunnerDuplicateException, ReportRunnerNameInvalidException,
            ReportScheduleMergeEmptyFormatSupportedException, ReportRunnerInvalidSortByException;
    }

    private ReportSchedule handleExceptions(Supplier supplier)
        throws UserAuthorizationRestException, ReportScheduleRestException, ReportScheduleValidationRestException {
        try {
            return supplier.execute();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportScheduleMissingNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.MISSING_NAME).withCause(e).build();
        } catch (ReportScheduleReportTypeMissingException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.MISSING_REPORT_TYPE).withCause(e).build();
        } catch (ReportScheduleMissingScheduleStartDateException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.MISSING_START_DATE).withCause(e).build();
        } catch (ReportScheduleMissingFrequencyException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.MISSING_FREQUENCY).withCause(e).build();
        } catch (ReportScheduleMissingParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_MISSING_PARAMETERS)
                .addParameter("parameters", e.getMissingParameters())
                .withCause(e).build();
        } catch (ReportScheduleInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_PARAMETER)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (ReportScheduleFormatNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_INVALID_FORMATS).withCause(e)
                .addParameter("formats", e.getFormats())
                .build();
        } catch (ReportScheduleMergeEmptyFormatSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_MERGE_EMPTY_FORMATS).withCause(e)
                .build();
        } catch (ReportScheduleReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_TYPE_NOT_FOUND).withCause(e)
                .build();
        } catch (ReportScheduleFrequencyNotSupportedForLegacySftpException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(
                    ReportScheduleValidationRestException.REPORT_SCHEDULE_FREQUENCY_NOT_SUPPORTED_FOR_LEGACY_SFTP)
                .withCause(e)
                .build();
        } catch (ReportScheduleInvalidScopesException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_SCOPES).withCause(e)
                .build();
        } catch (ReportScheduleSftpNotSupportedException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_SFTP_KEY_MISSING)
                .withCause(e)
                .build();
        } catch (SftpDestinationNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_SFTP_SERVER)
                .addParameter("sftp_server_id", e.getSftpDestinationId())
                .withCause(e)
                .build();
        } catch (ReportScheduleUpdateManagedByGitException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_LOCKED)
                .withCause(e)
                .build();
        } catch (ReportRunnerNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_NAME_ILLEGAL_CHARACTER)
                .withCause(e)
                .build();
        } catch (ReportRunnerInvalidSortByException e) {
            throw RestExceptionBuilder.newBuilder(ReportScheduleValidationRestException.class)
                .withErrorCode(ReportScheduleValidationRestException.REPORT_SCHEDULE_INVALID_SORT_BY)
                .addParameter("sort_by", e.getSortBy())
                .withCause(e)
                .build();
        } catch (ReportRunnerDuplicateException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }
}
