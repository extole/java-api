package com.extole.reporting.rest.impl.report.execution;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.report.execution.PublicReportResponse;
import com.extole.reporting.rest.report.execution.ReportNotFoundRestException;
import com.extole.reporting.rest.report.execution.ReportViewEndpoints;
import com.extole.reporting.service.ReportNotFoundException;
import com.extole.reporting.service.report.ReportContentFormatNotFoundException;
import com.extole.reporting.service.report.ReportContentNotFoundException;
import com.extole.reporting.service.report.ReportFormatInfo;
import com.extole.reporting.service.report.ReportService;

@Provider
public class ReportViewEndpointsImpl implements ReportViewEndpoints {
    private final CombinedAuthorizationProvider combinedAuthorizationProvider;
    private final ReportService reportService;
    private final ReportResponseMapper reportResponseMapper;
    private final HttpHeaders requestHeaders;

    @Autowired
    public ReportViewEndpointsImpl(CombinedAuthorizationProvider combinedAuthorizationProvider,
        ReportService reportService,
        ReportResponseMapper reportResponseMapper,
        @Context HttpHeaders requestHeaders) {
        this.combinedAuthorizationProvider = combinedAuthorizationProvider;
        this.reportService = reportService;
        this.reportResponseMapper = reportResponseMapper;
        this.requestHeaders = requestHeaders;
    }

    @Override
    public PublicReportResponse readPublicReport(String accessToken, String reportId, ZoneId timeZone)
        throws UserAuthorizationRestException, ReportNotFoundRestException {
        Authorization authorization = combinedAuthorizationProvider.getAuthorization(accessToken);
        Report report;
        try {
            report = reportService.getPublicReportById(authorization, Id.valueOf(reportId));
            List<ReportFormatInfo> formatInfo = Lists.newArrayList();
            for (Report.Format format : report.getFormats()) {
                try {
                    ReportFormatInfo reportFormatInfo = reportService.getReportInfo(authorization, Id.valueOf(reportId),
                        format);
                    formatInfo.add(reportFormatInfo);
                } catch (ReportContentNotFoundException | ReportContentFormatNotFoundException ignored) {
                    // ignored
                }
            }

            return reportResponseMapper.toPublicReportResponse(authorization, report, formatInfo, requestHeaders,
                timeZone);
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
}
