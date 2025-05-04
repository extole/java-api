package com.extole.reporting.rest.impl.report.type.mappers;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportTypeColumn;
import com.extole.reporting.entity.report.ReportTypeParameterDetails;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.ReportExecutorType;
import com.extole.reporting.rest.report.ReportParameterTypeName;
import com.extole.reporting.rest.report.ReportParameterTypeResponse;
import com.extole.reporting.rest.report.ReportTypeColumnResponse;
import com.extole.reporting.rest.report.ReportTypeParameterDetailsResponse;
import com.extole.reporting.rest.report.ReportTypeScope;
import com.extole.reporting.rest.report.ReportTypeVisibility;
import com.extole.reporting.rest.report.execution.ReportFormat;
import com.extole.reporting.rest.report.type.ReportTypeResponse;
import com.extole.reporting.rest.report.type.ReportTypeTagResponse;
import com.extole.reporting.rest.report.type.ReportTypeTagType;

public class BaseReportTypeResponseMapper {
    private static final String CLIENT_ID_PARAMETER = "client_id";
    private static final String CUSTOM_CATEGORY = "custom";
    private static final String CUSTOM_CLIENT_CATEGORY_PREFIX = "client_id:";

    public ReportTypeResponse.Builder applyRequestedChanges(Authorization authorization, ZoneId clientTimezone,
        ReportType reportType, ReportTypeResponse.Builder builder) {
        builder.withName(reportType.getName())
            .withDisplayName(reportType.getDisplayName())
            .withDescription(reportType.getDescription())
            .withExecutorType(ReportExecutorType.valueOf(reportType.getExecutorType().name()))
            .withCategories(computeCategories(authorization, reportType.getCategories()))
            .withScopes(toReportTypeScopes(authorization, reportType.getScopes()))
            .withVisibility(ReportTypeVisibility.valueOf(reportType.getVisibility().name()))
            .withParameters(toReportParameterTypeResponse(reportType.getParameters(authorization.getClientId())))
            .withFormats(reportType.getFormats().stream().map(format -> ReportFormat.valueOf(format.name()))
                .collect(Collectors.toList()))
            .withAllowedScopes(toReportTypeScopes(authorization, reportType.getAllowedScopes()))
            .withPreviewColumns(toRestReportTypeColumns(reportType.getPreviewColumns()))
            .withDataStart(reportType.getDataStart().atZone(clientTimezone))
            .withTags(reportType.getTags().stream().map(tag -> ReportTypeTagResponse.builder()
                .withName(tag.getName())
                .withType(ReportTypeTagType.valueOf(tag.getType().name()))
                .build())
                .collect(Collectors.toSet()));
        return builder;
    }

    private static List<ReportTypeParameterDetailsResponse>
        toReportParameterTypeResponse(List<ReportTypeParameterDetails> parameters) {
        return parameters.stream()
            .filter(parameter -> !parameter.getName().equalsIgnoreCase(CLIENT_ID_PARAMETER))
            .map(parameter -> new ReportTypeParameterDetailsResponse(
                parameter.getName(),
                parameter.getDisplayName(),
                parameter.getCategory().orElse(null),
                new ReportParameterTypeResponse(
                    ReportParameterTypeName.valueOf(parameter.getType().getName().name()),
                    ParameterValueType.valueOf(parameter.getType().getValueType().name()),
                    parameter.getType().getValues()),
                parameter.isRequired(),
                parameter.getOrder(),
                parameter.getDefaultValue().orElse(null),
                parameter.getDescription()))
            .distinct()
            .collect(Collectors.toList());
    }

    private static List<ReportTypeColumnResponse> toRestReportTypeColumns(List<ReportTypeColumn> previewColumns) {
        return previewColumns.stream()
            .map(column -> ReportTypeColumnResponse.builder()
                .withName(column.getName())
                .withSampleValue(column.getSampleValue().orElse(null))
                .withNote(column.getNote().orElse(null))
                .build())
            .collect(Collectors.toList());
    }

    private Set<ReportTypeScope> toReportTypeScopes(Authorization authorization, Set<ReportType.Scope> scopes) {
        Predicate<ReportType.Scope> scopeFilter = scope -> true;
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_SUPERUSER)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_SUPERUSER));
        }
        if (!authorization.getScopes().contains(Authorization.Scope.CLIENT_ADMIN)) {
            scopeFilter = scopeFilter.and(scope -> !scope.equals(ReportType.Scope.CLIENT_ADMIN));
        }

        return scopes.stream().filter(scopeFilter).map(ReportType.Scope::name)
            .map(ReportTypeScope::valueOf)
            .collect(Collectors.toSet());
    }

    private static List<String> computeCategories(Authorization authorization, List<String> categories) {
        return categories.stream()
            .filter(category -> !category.startsWith(CUSTOM_CLIENT_CATEGORY_PREFIX)
                || category.equals(CUSTOM_CLIENT_CATEGORY_PREFIX + authorization.getClientId().getValue()))
            .map(category -> category.startsWith(CUSTOM_CLIENT_CATEGORY_PREFIX) ? CUSTOM_CATEGORY : category)
            .collect(Collectors.toList());
    }
}
