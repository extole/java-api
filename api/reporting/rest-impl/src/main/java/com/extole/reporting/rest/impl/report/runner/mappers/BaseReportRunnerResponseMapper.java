package com.extole.reporting.rest.impl.report.runner.mappers;

import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.extole.authorization.service.Authorization;
import com.extole.id.Id;
import com.extole.reporting.entity.report.ReportParameter;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.runner.MergingConfiguration;
import com.extole.reporting.entity.report.runner.PauseInfo;
import com.extole.reporting.entity.report.runner.ReportRunner;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportParameterResponse;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.runner.MergingConfigurationResponse;
import com.extole.reporting.rest.report.runner.PauseInfoResponse;
import com.extole.reporting.rest.report.runner.ReportRunnerResponse;

public class BaseReportRunnerResponseMapper {
    private static final String LEGACY_TIME_ZONE_FORMAT_PARAMETER = "legacy_timezone_format";

    public ReportRunnerResponse.Builder applyRequestedChanges(Authorization authorization, ReportRunner reportRunner,
        ZoneId timezone, ReportRunnerResponse.Builder builder) {
        builder.withId(reportRunner.getId().toString())
            .withName(reportRunner.getName())
            .withReportType(reportRunner.getReportTypeName())
            .withFormats(reportRunner.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                .collect(Collectors.toList()))
            .withCreatedDate(reportRunner.getCreatedDate().atZone(timezone))
            .withUpdatedDate(reportRunner.getUpdatedDate().atZone(timezone))
            .withParameters(toReportParametersResponse(reportRunner))
            .withScopes(toReportScopes(authorization, reportRunner))
            .withTags(reportRunner.getTags())
            .withUserId(reportRunner.getUserId().getValue())
            .withSftpServerId(reportRunner.getSftpServerId().map(Id::getValue).orElse(null))
            .withReportNamePattern(reportRunner.getReportNamePattern())
            .withSftpReportNamePattern(reportRunner.getSftpReportNamePattern());
        reportRunner.getPauseInfo().map(this::toPauseInfoResponse).ifPresent(builder::withPauseInfo);
        reportRunner.getMergingConfiguration().map(this::toMergingConfigurationResponse)
            .ifPresent(builder::withMergingConfiguration);
        reportRunner.getDeliverEmptyReportsToSftp().ifPresent(builder::withDeliverEmptyReportsToSftp);
        return builder;
    }

    private Map<String, ReportParameterResponse> toReportParametersResponse(ReportRunner reportRunner) {
        return reportRunner.getParameters().stream()
            .filter(parameter -> !parameter.getDetails().getName().equalsIgnoreCase(LEGACY_TIME_ZONE_FORMAT_PARAMETER))
            .collect(Collectors.toMap(parameter -> parameter.getDetails().getName(),
                parameter -> toReportParameterResponse(parameter)));
    }

    private ReportParameterResponse toReportParameterResponse(ReportParameter parameter) {
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

    private PauseInfoResponse toPauseInfoResponse(PauseInfo pauseInfo) {
        PauseInfoResponse.Builder pauseInfoBuilder = PauseInfoResponse.builder();
        pauseInfoBuilder.withUserId(pauseInfo.getUserId());
        pauseInfoBuilder.withUpdatedDate(pauseInfo.getUpdatedDate());
        pauseInfo.getDescription().ifPresent(pauseInfoBuilder::withDescription);
        return pauseInfoBuilder.build();
    }

    private MergingConfigurationResponse toMergingConfigurationResponse(MergingConfiguration mergingConfiguration) {
        MergingConfigurationResponse.Builder configurationBuilder = MergingConfigurationResponse.builder();
        configurationBuilder.withSortBy(mergingConfiguration.getSortBy());
        configurationBuilder.withUniqueBy(mergingConfiguration.getUniqueBy());
        configurationBuilder.withFormats(mergingConfiguration.getFormats().stream()
            .map(format -> ReportFormat.valueOf(format.name())).collect(Collectors.toSet()));
        return configurationBuilder.build();
    }

    private Set<ReportTypeScope> toReportScopes(Authorization authorization, ReportRunner reportRunner) {
        Predicate<ReportType.Scope> scopeFilter = scope -> true;
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_SUPERUSER));
        }
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_ADMIN)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_ADMIN));
        }

        return reportRunner.getScopes().stream().filter(scopeFilter).map(ReportType.Scope::name)
            .map(ReportTypeScope::valueOf)
            .collect(Collectors.toSet());
    }
}
