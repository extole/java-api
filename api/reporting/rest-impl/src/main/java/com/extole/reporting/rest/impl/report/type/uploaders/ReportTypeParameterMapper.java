package com.extole.reporting.rest.impl.report.type.uploaders;

import com.extole.model.entity.report.type.ReportParameterType;
import com.extole.reporting.rest.report.ParameterValueType;
import com.extole.reporting.rest.report.type.DynamicReportTypeParameterDetailsRequest;
import com.extole.reporting.rest.report.type.ReportTypeParameterDetailsRequest;

public final class ReportTypeParameterMapper {

    private ReportTypeParameterMapper() {
    }

    public static ReportTypeParameterDetailsImpl map(ReportTypeParameterDetailsRequest request) {
        ReportTypeParameterDetailsImpl.Builder builder = ReportTypeParameterDetailsImpl.builder()
            .withName(request.getName());
        request.getDisplayName().ifPresent(builder::withDisplayName);
        request.getOrder().ifPresent(builder::withOrder);
        request.getCategory().ifPresent(builder::withCategory);
        request.getIsRequired().ifPresent(builder::withIsRequired);
        request.getDefaultValue().ifPresent(builder::withDefaultValue);
        request.getDescription().ifPresent(builder::withDescription);

        if (request.getType().equals(ParameterValueType.DYNAMIC)) {
            DynamicReportTypeParameterDetailsRequest dynamicRequest =
                (DynamicReportTypeParameterDetailsRequest) request;
            ReportParameterType.Builder parameterTypeBuilder = ReportParameterType.builder()
                .withValueType(ReportParameterType.ParameterValueType.DYNAMIC);
            dynamicRequest.getTypeName().map(com.extole.reporting.rest.report.ReportParameterTypeName::name)
                .map(ReportParameterType.ReportParameterTypeName::valueOf)
                .ifPresent(parameterTypeBuilder::withTypeName);
            dynamicRequest.getValues().ifPresent(parameterTypeBuilder::withValues);
            builder.withType(parameterTypeBuilder.build());
        }
        return builder.build();
    }
}
