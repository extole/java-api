package com.extole.reporting.rest.impl.fixup;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.Fixup;
import com.extole.reporting.entity.fixup.FixupExecution;
import com.extole.reporting.entity.fixup.filter.FixupFilter;
import com.extole.reporting.entity.fixup.filter.FixupFilterType;
import com.extole.reporting.entity.fixup.transformation.FixupTransformation;
import com.extole.reporting.entity.fixup.transformation.FixupTransformationType;
import com.extole.reporting.rest.fixup.FixupDataSource;
import com.extole.reporting.rest.fixup.FixupExecutionResponse;
import com.extole.reporting.rest.fixup.FixupResponse;
import com.extole.reporting.rest.fixup.filter.FixupFilterResponse;
import com.extole.reporting.rest.fixup.transformation.FixupTransformationResponse;
import com.extole.reporting.rest.impl.fixup.filter.FixupFilterRestMapper;
import com.extole.reporting.rest.impl.fixup.transformation.FixupTransformationRestMapper;

@Component
public class FixupRestMapper {
    private final Map<FixupFilterType, FixupFilterRestMapper<FixupFilter, FixupFilterResponse>> filterRestMappersByType;
    private final Map<FixupTransformationType, FixupTransformationRestMapper<FixupTransformation,
        FixupTransformationResponse>> transformationRestMappersByType;

    @Autowired
    @SuppressWarnings({"rawtypes", "unchecked"})
    public FixupRestMapper(List<FixupFilterRestMapper> filterMappers,
        List<FixupTransformationRestMapper> transformationMappers) {
        Map<FixupFilterType, FixupFilterRestMapper<FixupFilter, FixupFilterResponse>> filterRestMappers =
            new HashMap<>();
        for (FixupFilterRestMapper mapper : filterMappers) {
            filterRestMappers.put(mapper.getType(), mapper);
        }
        Map<FixupTransformationType,
            FixupTransformationRestMapper<FixupTransformation, FixupTransformationResponse>> transformationRestMappers =
                new HashMap<>();
        for (FixupTransformationRestMapper mapper : transformationMappers) {
            transformationRestMappers.put(mapper.getType(), mapper);
        }
        this.filterRestMappersByType = ImmutableMap.copyOf(filterRestMappers);
        this.transformationRestMappersByType = ImmutableMap.copyOf(transformationRestMappers);
    }

    public FixupResponse toFixupResponse(Fixup fixup, List<FixupExecution> executions, ZoneId timeZone) {
        ZonedDateTime createdDate = ZonedDateTime.ofInstant(fixup.getCreatedDate(), timeZone);
        return new FixupResponse(fixup.getId().getValue(),
            FixupDataSource.valueOf(fixup.getDataSource().name()),
            fixup.getName(),
            fixup.getDescription().orElse(null), fixup.getFilter().map(this::toFilterResponse).orElse(null),
            fixup.getTransformation().map(this::toTransformationResponse).orElse(null),
            mapExecutions(executions, timeZone), createdDate);
    }

    private List<FixupExecutionResponse> mapExecutions(List<FixupExecution> executions, ZoneId timeZone) {
        List<FixupExecutionResponse> executionResponses = Lists.newArrayList();
        executions.forEach(execution -> executionResponses.add(new FixupExecutionResponse(execution.getId().getValue(),
            execution.getUserId().getValue(), ZonedDateTime.ofInstant(execution.getStartDate(), timeZone),
            execution.getEndDate().map(date -> ZonedDateTime.ofInstant(date, timeZone)).orElse(null),
            execution.getStatus().name(), execution.getErrorCode().map(code -> code.name()).orElse(null),
            execution.getErrorMessage().orElse(null), execution.getEventCount().orElse(null))));
        return executionResponses;
    }

    private FixupFilterResponse toFilterResponse(FixupFilter filter) {
        return filterRestMappersByType.get(filter.getType()).toResponse(filter);
    }

    private FixupTransformationResponse toTransformationResponse(FixupTransformation transformation) {
        return transformationRestMappersByType.get(transformation.getType()).toResponse(transformation);
    }
}
