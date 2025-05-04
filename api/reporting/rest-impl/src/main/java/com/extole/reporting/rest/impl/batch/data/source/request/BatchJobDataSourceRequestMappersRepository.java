package com.extole.reporting.rest.impl.batch.data.source.request;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

@Component
@SuppressWarnings("rawtypes")
public class BatchJobDataSourceRequestMappersRepository {

    private final Map<BatchJobDataSourceType, BatchJobDataSourceRequestMapper> dataSourceMappers;

    @Autowired
    public BatchJobDataSourceRequestMappersRepository(List<BatchJobDataSourceRequestMapper> dataSourceMappers) {
        this.dataSourceMappers = dataSourceMappers.stream().collect(Collectors.toMap(item -> item.getType(),
            Function.identity()));
    }

    public BatchJobDataSourceRequestMapper getDataSourceMapper(BatchJobDataSourceType type) {
        BatchJobDataSourceRequestMapper mapper = dataSourceMappers.get(type);
        if (mapper == null) {
            throw new RuntimeException("Mapper of type=" + type + " not found");
        }
        return mapper;
    }
}
