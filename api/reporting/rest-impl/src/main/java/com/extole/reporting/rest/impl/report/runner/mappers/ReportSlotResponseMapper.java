package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.model.entity.report.type.ReportParameter;
import com.extole.reporting.entity.report.runner.ReportSlot;
import com.extole.reporting.rest.impl.report.ReportResponseMapper;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.execution.ReportResponse;
import com.extole.reporting.rest.report.runner.BaseReportRunnerReportResponse;
import com.extole.reporting.rest.report.runner.ExecutedReportRunnerReportResponse;
import com.extole.reporting.rest.report.runner.NotExecutedReportRunnerReportResponse;
import com.extole.reporting.rest.report.runner.ReportSlotStatus;
import com.extole.reporting.rest.report.runner.RollingReportRunnerReportResponse;

@Component
public class ReportSlotResponseMapper {
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String LEGACY_TIME_ZONE_FORMAT_PARAMETER = "legacy_timezone_format";

    private final ReportResponseMapper reportResponseMapper;

    @Autowired
    public ReportSlotResponseMapper(ReportResponseMapper reportResponseMapper) {
        this.reportResponseMapper = reportResponseMapper;
    }

    public BaseReportRunnerReportResponse toResponse(Authorization authorization, ReportSlot reportSlot,
        HttpHeaders requestHeaders,
        ZoneId timezone) {
        if (reportSlot.getReport().isPresent()) {
            ReportResponse report = reportResponseMapper.toReportResponse(authorization, reportSlot.getReport().get(),
                requestHeaders, timezone);

            if (reportSlot.getTags().stream().anyMatch(tag -> tag.startsWith("internal:report-runner-roll:"))) {
                RollingReportRunnerReportResponse.Builder builder = RollingReportRunnerReportResponse.builder();
                builder.withReportId(report.getReportId())
                    .withExecutorType(report.getExecutorType())
                    .withFormat(report.getFormat())
                    .withFormats(report.getFormats())
                    .withUserId(report.getUserId())
                    .withVisible(report.isVisible())
                    .withScopes(report.getScopes())
                    .withCreatedDate(report.getCreatedDate())
                    .withErrorCode(report.getErrorCode())
                    .withDownloadUri(report.getDownloadUri())
                    .withSftpServerId(report.getSftpServerId())
                    .withSftpReportName(report.getSftpReportName())
                    .withStatus(ReportSlotStatus.valueOf(report.getStatus().name()))
                    .withReportType(reportSlot.getReportType())
                    .withDisplayName(reportSlot.getDisplayName())
                    .withParameters(toReportParametersResponse(reportSlot.getParameters()))
                    .withTags(reportSlot.getTags());
                report.getStartedDate().ifPresent(builder::withStartedDate);
                report.getCompletedDate().ifPresent(builder::withCompletedDate);
                return builder.build();
            } else {
                ExecutedReportRunnerReportResponse.Builder builder = ExecutedReportRunnerReportResponse.builder()
                    .withSlot(reportSlot.getSlot().toString())
                    .withReportId(report.getReportId())
                    .withExecutorType(report.getExecutorType())
                    .withFormat(report.getFormat())
                    .withFormats(report.getFormats())
                    .withUserId(report.getUserId())
                    .withVisible(report.isVisible())
                    .withScopes(report.getScopes())
                    .withCreatedDate(report.getCreatedDate())
                    .withErrorCode(report.getErrorCode())
                    .withDownloadUri(report.getDownloadUri())
                    .withSftpServerId(report.getSftpServerId())
                    .withSftpReportName(report.getSftpReportName())
                    .withStatus(ReportSlotStatus.valueOf(report.getStatus().name()));
                builder
                    .withReportType(reportSlot.getReportType())
                    .withDisplayName(reportSlot.getDisplayName())
                    .withParameters(toReportParametersResponse(reportSlot.getParameters()))
                    .withTags(reportSlot.getTags());
                report.getStartedDate().ifPresent(builder::withStartedDate);
                report.getCompletedDate().ifPresent(builder::withCompletedDate);
                return builder.build();
            }
        } else {
            NotExecutedReportRunnerReportResponse.Builder builder = NotExecutedReportRunnerReportResponse.builder()
                .withSlot(reportSlot.getSlot().toString());
            builder.withReportType(reportSlot.getReportType())
                .withDisplayName(reportSlot.getDisplayName())
                .withParameters(toReportParametersResponse(reportSlot.getParameters()))
                .withTags(reportSlot.getTags());
            return builder.build();
        }
    }

    private static Map<String, ReportParameterResponse>
        toReportParametersResponse(List<ReportParameter> reportParameters) {
        return reportParameters.stream()
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(CLIENT_ID_PARAMETER))
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(LEGACY_TIME_ZONE_FORMAT_PARAMETER))
            .collect(Collectors.toMap(parameter -> parameter.getDetails().getName(),
                ReportSlotResponseMapper::toReportParameterResponse));
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
}
