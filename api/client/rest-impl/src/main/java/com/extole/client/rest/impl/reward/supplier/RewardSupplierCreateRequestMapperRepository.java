package com.extole.client.rest.impl.reward.supplier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.reward.supplier.RewardSupplierType;

@Component
@SuppressWarnings("rawtypes")
public class RewardSupplierCreateRequestMapperRepository {

    private final Map<RewardSupplierType, RewardSupplierCreateRequestMapper> mappersByType;

    @Autowired
    public RewardSupplierCreateRequestMapperRepository(List<RewardSupplierCreateRequestMapper> mappers) {
        Map<RewardSupplierType, RewardSupplierCreateRequestMapper> mappersMap = new HashMap<>();
        for (RewardSupplierCreateRequestMapper mapper : mappers) {
            if (mappersMap.containsKey(mapper.getType())) {
                throw new IllegalStateException(
                    "Found multiple instances of RewardSupplierCreateRequestMapper for the same type: "
                        + mapper.getType());
            }
            mappersMap.put(mapper.getType(), mapper);
        }
        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public RewardSupplierCreateRequestMapper getRewardSupplierCreateRequestMapper(RewardSupplierType type) {
        RewardSupplierCreateRequestMapper mapper = mappersByType.get(type);
        if (mapper == null) {
            mapper = mappersByType.get(RewardSupplierType.CUSTOM_REWARD);
        }
        return mapper;
    }

}
