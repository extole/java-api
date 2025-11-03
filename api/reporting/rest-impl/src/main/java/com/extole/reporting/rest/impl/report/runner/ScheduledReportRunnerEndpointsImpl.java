package com.extole.reporting.rest.impl.report.runner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.report.runner.ReportRunnerInvalidParametersException;
import com.extole.model.service.report.runner.ReportRunnerNotFoundException;
import com.extole.model.service.report.runner.ReportingServiceException;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.runner.ReportRunnerRestException;
import com.extole.reporting.rest.report.runner.ReportRunnerValidationRestException;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerEndpoints;
import com.extole.reporting.rest.report.runner.ScheduledReportRunnerRestException;
import com.extole.reporting.service.report.runner.ReportRunnerWrongTypeException;
import com.extole.reporting.service.report.runner.ScheduledReportRunnerService;

@Provider
public class ScheduledReportRunnerEndpointsImpl implements ScheduledReportRunnerEndpoints {
    private final ClientAuthorizationProvider authorizationProvider;
    private final ScheduledReportRunnerService scheduledReportRunnerService;
    private final ReportResponseMapper reportResponseMapper;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ScheduledReportRunnerEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ScheduledReportRunnerService scheduledReportRunnerService,
        ReportResponseMapper reportResponseMapper,
        @Context HttpHeaders requestHeaders) {
        this.authorizationProvider = authorizationProvider;
        this.scheduledReportRunnerService = scheduledReportRunnerService;
        this.reportResponseMapper = reportResponseMapper;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public List<ReportResponse> scheduleMissingReports(String accessToken, String reportRunnerId,
        Optional<ZonedDateTime> slotRequest, Optional<Integer> missingReportsToGenerate, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportRunnerRestException, ReportRunnerValidationRestException,
        ScheduledReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Report> reports =
                scheduledReportRunnerService.scheduleMissingReports(authorization, Id.valueOf(reportRunnerId),
                    slotRequest, missingReportsToGenerate);
            List<ReportResponse> reportResponses = Lists.newArrayList();
            for (Report report : reports) {
                reportResponses
                    .add(reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timeZone));
            }
            return reportResponses;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (ReportRunnerInvalidParametersException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerValidationRestException.class)
                .withErrorCode(ReportRunnerValidationRestException.REPORT_RUNNER_INVALID_PARAMETER)
                .addParameter("parameters", e.getParameterNames())
                .withCause(e).build();
        } catch (ReportRunnerWrongTypeException e) {
            throw RestExceptionBuilder.newBuilder(ScheduledReportRunnerRestException.class)
                .withErrorCode(ScheduledReportRunnerRestException.REPORT_RUNNER_WRONG_TYPE)
                .withCause(e).build();
        } catch (ReportingServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    @Override
    public List<ReportResponse> deleteReports(String accessToken, String reportRunnerId,
        Optional<ZonedDateTime> slotRequest, ZoneId timezone)
        throws UserAuthorizationRestException, ReportRunnerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            List<Report> reports =
                scheduledReportRunnerService.deleteReports(authorization, Id.valueOf(reportRunnerId), slotRequest);
            return reports.stream().map(
                report -> reportResponseMapper.toReportResponse(authorization, report, requestHeaders, timezone))
                .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (ReportRunnerNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportRunnerRestException.class)
                .withErrorCode(ReportRunnerRestException.REPORT_RUNNER_NOT_FOUND)
                .addParameter("report_runner_id", reportRunnerId)
                .withCause(e).build();
        } catch (ReportingServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }
}
