package com.extole.reporting.rest.impl.audience.operation;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationType;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateResponse;
import com.extole.reporting.rest.impl.audience.operation.action.ActionAudienceOperationStateResponseMapper;
import com.extole.reporting.rest.impl.audience.operation.modification.ModificationAudienceOperationStateResponseMapper;
import com.extole.spring.ServiceLocator;

@Component
public class AudienceOperationStateResponseMapperRegistry {

    private final ServiceLocator serviceLocator;

    @Autowired
    public AudienceOperationStateResponseMapperRegistry(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    private static final Map<AudienceOperationType,
        Class<? extends AudienceOperationStateResponseMapper<?, ?>>> MAPPER_CLASSES_BY_TYPE =
            ImmutableMap.<AudienceOperationType, Class<? extends AudienceOperationStateResponseMapper<?, ?>>>builder()
                .put(AudienceOperationType.ADD, ModificationAudienceOperationStateResponseMapper.class)
                .put(AudienceOperationType.REMOVE, ModificationAudienceOperationStateResponseMapper.class)
                .put(AudienceOperationType.REPLACE, ModificationAudienceOperationStateResponseMapper.class)
                .put(AudienceOperationType.ACTION, ActionAudienceOperationStateResponseMapper.class)
                .build();

    @SuppressWarnings("unchecked")
    public <RESPONSE extends AudienceOperationStateResponse, DEBUG_RESPONSE extends AudienceOperationStateDebugResponse>
        AudienceOperationStateResponseMapper<RESPONSE, DEBUG_RESPONSE> getMapper(AudienceOperationType type) {
        Class<? extends AudienceOperationStateResponseMapper<RESPONSE, DEBUG_RESPONSE>> mapperClass =
            (Class<? extends AudienceOperationStateResponseMapper<RESPONSE, DEBUG_RESPONSE>>) MAPPER_CLASSES_BY_TYPE
                .get(type);
        if (mapperClass == null) {
            throw new IllegalStateException("Unsupported audience operation type: " + type);
        }

        return serviceLocator.lookupSingleton(mapperClass);
    }

}
