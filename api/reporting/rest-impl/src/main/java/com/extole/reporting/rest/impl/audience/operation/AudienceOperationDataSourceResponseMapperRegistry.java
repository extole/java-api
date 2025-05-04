package com.extole.reporting.rest.impl.audience.operation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSource;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;

@Component
public class AudienceOperationDataSourceResponseMapperRegistry {

    private final Map<AudienceOperationDataSourceType, AudienceOperationDataSourceResponseMapper<?, ?>> mappersByType;

    @Autowired
    public AudienceOperationDataSourceResponseMapperRegistry(
        List<AudienceOperationDataSourceResponseMapper<?, ?>> mappers) {
        this.mappersByType = mappers.stream().collect(Collectors.toMap(item -> item.getType(),
            Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <FROM extends AudienceOperationDataSource, TO extends AudienceOperationDataSourceResponse>
        AudienceOperationDataSourceResponseMapper<FROM, TO> getMapper(AudienceOperationDataSourceType type) {
        AudienceOperationDataSourceResponseMapper<FROM, TO> mapper =
            (AudienceOperationDataSourceResponseMapper<FROM, TO>) mappersByType.get(type);
        if (mapper == null) {
            throw new IllegalStateException("Unsupported data source type: " + type);
        }

        return mapper;
    }

}
