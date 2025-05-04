package com.extole.reporting.rest.impl.report.runner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.runner.ReportRunnerType;
import com.extole.reporting.rest.impl.report.runner.mappers.ReportRunnerViewResponseMapper;

@Component
public class ReportRunnerViewResponseMappersRepository {
    private final Map<ReportRunnerType, ReportRunnerViewResponseMapper<?>> reportRunnerMappersByType;

    @Autowired
    public ReportRunnerViewResponseMappersRepository(
        List<ReportRunnerViewResponseMapper<?>> reportRunnerResponseMappers) {
        Map<ReportRunnerType, ReportRunnerViewResponseMapper<?>> reportRunnerMappersMap = Maps.newHashMap();
        for (ReportRunnerViewResponseMapper<?> reportRunnerResponseMapper : reportRunnerResponseMappers) {
            if (reportRunnerMappersMap.containsKey(reportRunnerResponseMapper.getType())) {
                throw new ReportRunnerRestRuntimeException(
                    "Found multiple instances of ReportRunnerResponseMapper for the same type: "
                        + reportRunnerResponseMapper.getType());
            }
            reportRunnerMappersMap.put(reportRunnerResponseMapper.getType(), reportRunnerResponseMapper);
        }
        this.reportRunnerMappersByType = Collections.unmodifiableMap(reportRunnerMappersMap);
    }

    public ReportRunnerViewResponseMapper<?> getReportRunnerResponseMapper(ReportRunnerType type) {
        ReportRunnerViewResponseMapper<?> mapper = reportRunnerMappersByType.get(type);
        if (mapper == null) {
            throw new ReportRunnerRestRuntimeException("Unsupported report runner type: " + type);
        }
        return mapper;
    }
}
