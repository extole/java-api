package com.extole.reporting.rest.impl.report.runner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.impl.report.runner.mappers.ReportRunnerResponseMapper;

@Component
public class ReportRunnerResponseMappersRepository {
    private final Map<ReportRunnerType, ReportRunnerResponseMapper<?>> reportRunnerMappersByType;

    @Autowired
    public ReportRunnerResponseMappersRepository(List<ReportRunnerResponseMapper<?>> reportRunnerResponseMappers) {
        Map<ReportRunnerType, ReportRunnerResponseMapper<?>> reportRunnerMappersMap = Maps.newHashMap();
        for (ReportRunnerResponseMapper<?> reportRunnerResponseMapper : reportRunnerResponseMappers) {
            if (reportRunnerMappersMap.containsKey(reportRunnerResponseMapper.getType())) {
                throw new ReportRunnerRestRuntimeException(
                    "Found multiple instances of ReportRunnerResponseMapper for the same type: "
                        + reportRunnerResponseMapper.getType());
            }
            reportRunnerMappersMap.put(reportRunnerResponseMapper.getType(), reportRunnerResponseMapper);
        }
        this.reportRunnerMappersByType = Collections.unmodifiableMap(reportRunnerMappersMap);
    }

    public ReportRunnerResponseMapper<?> getReportRunnerResponseMapper(ReportRunnerType type) {
        ReportRunnerResponseMapper<?> mapper = reportRunnerMappersByType.get(type);
        if (mapper == null) {
            throw new RuntimeException("Unsupported report runner type: " + type);
        }
        return mapper;
    }
}
