package com.extole.reporting.rest.impl.report;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.report.type.ReportParameter;
import com.extole.model.entity.report.type.ReportType;
import com.extole.reporting.entity.report.Report;
import com.extole.reporting.entity.report.ReportResult;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.FormatReportInfoResponse;
import com.extole.reporting.rest.report.execution.PublicReportResponse;
import com.extole.reporting.rest.report.execution.PublicReportResultResponse;
import com.extole.reporting.rest.report.execution.ReportEndpoints;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.execution.ReportStatus;
import com.extole.reporting.service.report.ReportFormatInfo;

@Component
public class ReportResponseMapper {
    private static final String REPORT_ID_PARAM = "{reportId}";
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String LEGACY_TIME_ZONE_FORMAT_PARAMETER = "legacy_timezone_format";
    private static final String HEADER_X_EXTOLE_INCOMING_URL = "X-Extole-Incoming-Url";

    private final String environment;
    private final String downloadContentUrlTemplate;

    @Autowired
    public ReportResponseMapper(@Value("${extole.environment:lo}") String environment,
        @Value("${reporting.report.download.url:https://api%s.extole.io}") String downloadContentUrl) {
        this.environment = environment.toLowerCase();
        this.downloadContentUrlTemplate = String.format(downloadContentUrl, getEnvironmentUriPrefix()) +
            ReportEndpoints.REPORT_URI + ReportEndpoints.DOWNLOAD_URI.replace(REPORT_ID_PARAM, "%s");
    }

    public ReportResponse toReportResponse(Authorization authorization, Report report, HttpHeaders requestHeaders,
        ZoneId timezone) {
        try {
            String reportContentUrl = buildReportContentUrl(report, requestHeaders);
            ReportResult reportResult = report.getLastResult();

            ReportResponse.Builder builder = ReportResponse.builder()
                .withReportId(report.getId().getValue())
                .withReportType(report.getName())
                .withDisplayName(report.getDisplayName())
                .withExecutorType(ReportExecutorType.valueOf(report.getExecutorType().name()))
                .withFormat(ReportFormat.valueOf(report.getFormats().stream().findFirst().get().name()))
                .withFormats(report.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                    .collect(Collectors.toList()))
                .withStatus(ReportStatus.valueOf(reportResult.getStatus().name()))
                .withUserId(report.getUserId().getValue())
                .withParameters(toReportParametersResponse(report))
                .withVisible(Boolean.valueOf(reportResult.getScopes().contains(ReportType.Scope.CLIENT_ADMIN)))
                .withScopes(toReportScopes(authorization, reportResult))
                .withTags(report.getTags())
                .withCreatedDate(report.getCreatedDate().atZone(timezone))
                .withStartedDate(reportResult.getStartedDate().map(value -> value.atZone(timezone)).orElse(null))
                .withCompletedDate(reportResult.getCompletedDate().map(value -> value.atZone(timezone)).orElse(null))
                .withErrorCode(reportResult.getErrorCode().map(Object::toString).orElse(null))
                .withDownloadUri(reportContentUrl)
                .withSftpReportName(report.getSftpReportName().orElse(null));

            report.getSftpServerId().map(Id::getValue).ifPresent(builder::withSftpServerId);
            return builder.build();
        } catch (URISyntaxException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    public PublicReportResponse toPublicReportResponse(Authorization authorization, Report report,
        List<ReportFormatInfo> formatInfo, HttpHeaders requestHeaders, ZoneId timezone) {
        try {
            String reportContentUrl = buildReportContentUrl(report, requestHeaders);
            ReportResult reportResult = report.getLastResult();

            PublicReportResponse.Builder builder = PublicReportResponse.builder()
                .withReportId(report.getId().getValue())
                .withReportType(report.getName())
                .withDisplayName(report.getDisplayName())
                .withExecutorType(ReportExecutorType.valueOf(report.getExecutorType().name()))
                .withFormats(report.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                    .collect(Collectors.toList()))
                .withUserId(report.getUserId().getValue())
                .withResult(PublicReportResultResponse.builder()
                    .withStatus(ReportStatus.valueOf(reportResult.getStatus().name()))
                    .withStartedDate(reportResult.getStartedDate().map(value -> value.atZone(timezone)).orElse(null))
                    .withCompletedDate(
                        reportResult.getCompletedDate().map(value -> value.atZone(timezone)).orElse(null))
                    .withTotalRows(Long.valueOf(reportResult.getTotalRows()))
                    .withFormatsInfo(formatInfo.stream().collect(
                        Collectors.toMap(
                            entry -> ReportFormat.valueOf(entry.getFormat().name()),
                            entry -> new FormatReportInfoResponse(
                                entry.getContentLength(),
                                entry.getTotalRows()))))
                    .build())
                .withParameters(toReportParametersResponse(report))
                .withTags(report.getTags())
                .withCreatedDate(report.getCreatedDate().atZone(timezone))
                .withDownloadUri(reportContentUrl)
                .withScopes(toReportScopes(authorization, reportResult));

            return builder.build();
        } catch (URISyntaxException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        }
    }

    private String buildReportContentUrl(Report report, HttpHeaders requestHeaders) throws URISyntaxException {
        String reportContentUrl;
        if (requestHeaders != null && requestHeaders.getRequestHeader(HEADER_X_EXTOLE_INCOMING_URL) != null
            && !requestHeaders.getRequestHeader(HEADER_X_EXTOLE_INCOMING_URL).isEmpty()) {
            String incomingUrl = requestHeaders.getRequestHeader(HEADER_X_EXTOLE_INCOMING_URL).get(0);
            String[] urlParts = incomingUrl.split(ReportEndpoints.REPORT_URI);
            if (urlParts.length == 2) {
                reportContentUrl = String.format(urlParts[0] + ReportEndpoints.REPORT_URI
                    + ReportEndpoints.DOWNLOAD_URI.replace(REPORT_ID_PARAM, "%s"), report.getId().getValue());
            } else {
                URI uri = new URI(incomingUrl);
                reportContentUrl = String.format("https://" + uri.getHost() + ReportEndpoints.REPORT_URI +
                    ReportEndpoints.DOWNLOAD_URI.replace(REPORT_ID_PARAM, "%s"), report.getId().getValue());
            }
        } else {
            reportContentUrl = String.format(downloadContentUrlTemplate, report.getId().getValue());
        }
        return reportContentUrl;
    }

    private Set<ReportTypeScope> toReportScopes(Authorization authorization, ReportResult reportResult) {
        Predicate<ReportType.Scope> scopeFilter = scope -> true;
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_SUPERUSER));
        }
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_ADMIN)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_ADMIN));
        }

        return reportResult.getScopes().stream().filter(scopeFilter).map(ReportType.Scope::name)
            .map(ReportTypeScope::valueOf)
            .collect(Collectors.toSet());
    }

    private static Map<String, ReportParameterResponse> toReportParametersResponse(Report report) {
        return report.getParameters().stream()
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(CLIENT_ID_PARAMETER))
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(LEGACY_TIME_ZONE_FORMAT_PARAMETER))
            .collect(Collectors.toMap(parameter -> parameter.getDetails().getName(),
                ReportResponseMapper::toReportParameterResponse));
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

    private String getEnvironmentUriPrefix() {
        switch (environment) {
            case "pr":
                return "";
            default:
                return "." + environment;
        }
    }
}
