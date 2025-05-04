package com.extole.reporting.rest.impl.report.type;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.entity.report.ReportType.Type;
import com.extole.reporting.rest.impl.report.execution.ReportRuntimeException;
import com.extole.reporting.rest.impl.report.type.mappers.ReportTypeResponseMapper;

@Component
public class ReportTypeResponseMappersRepository {
    private final Map<Type, ReportTypeResponseMapper<? extends ReportType>> reportTypeMappersByType;

    @Autowired
    public ReportTypeResponseMappersRepository(
        List<ReportTypeResponseMapper<? extends ReportType>> reportTypeResponseMappers) {
        Map<Type, ReportTypeResponseMapper<? extends ReportType>> reportTypeResponseMapperMap = Maps.newHashMap();
        for (ReportTypeResponseMapper<? extends ReportType> reportTypeResponseMapper : reportTypeResponseMappers) {
            if (reportTypeResponseMapperMap.containsKey(reportTypeResponseMapper.getType())) {
                throw new ReportRuntimeException(
                    "Found multiple instances of ReportTypeResponseMapper for the same type: "
                        + reportTypeResponseMapper.getType());
            }
            reportTypeResponseMapperMap.put(reportTypeResponseMapper.getType(), reportTypeResponseMapper);
        }
        this.reportTypeMappersByType = Collections.unmodifiableMap(reportTypeResponseMapperMap);
    }

    public ReportTypeResponseMapper<?> getReportTypeResponseMapper(Type type) {
        ReportTypeResponseMapper<?> mapper = reportTypeMappersByType.get(type);
        if (mapper == null) {
            throw new ReportRuntimeException("Unsupported report type: " + type);
        }
        return mapper;
    }
}
