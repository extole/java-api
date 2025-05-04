package com.extole.reporting.rest.impl.batch.data.source.response;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType;

@Component
@SuppressWarnings("rawtypes")
public class BatchJobDataSourceResponseMappersRepository {

    private final Map<BatchJobDataSourceType, BatchJobDataSourceResponseMapper> dataSourceResponseMapperMap;

    @Autowired
    public BatchJobDataSourceResponseMappersRepository(
        List<BatchJobDataSourceResponseMapper> dataSourceResponseMapperMap) {
        this.dataSourceResponseMapperMap = dataSourceResponseMapperMap.stream()
            .collect(Collectors.toMap(item -> item.getType(), Function.identity()));
    }

    public BatchJobDataSourceResponseMapper getMapper(BatchJobDataSourceType type) {
        BatchJobDataSourceResponseMapper mapper = dataSourceResponseMapperMap.get(type);
        if (mapper == null) {
            throw new RuntimeException("Mapper of type=" + type + " not found");
        }
        return mapper;
    }
}
