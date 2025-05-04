package com.extole.client.rest.impl.reward.supplier.built;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.reward.supplier.RewardSupplierType;

@Component
@SuppressWarnings("rawtypes")
public class BuiltRewardSupplierResponseMapperRepository {

    private final Map<RewardSupplierType, BuiltRewardSupplierResponseMapper> mappersByType;

    @Autowired
    public BuiltRewardSupplierResponseMapperRepository(List<BuiltRewardSupplierResponseMapper> mappers) {
        Map<RewardSupplierType, BuiltRewardSupplierResponseMapper> mappersMap = new HashMap<>();
        for (BuiltRewardSupplierResponseMapper mapper : mappers) {
            if (mappersMap.containsKey(mapper.getType())) {
                throw new IllegalStateException(
                    "Found multiple instances of BuiltRewardSupplierResponseMapper for the same type: "
                        + mapper.getType());
            }
            mappersMap.put(mapper.getType(), mapper);
        }
        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public BuiltRewardSupplierResponseMapper getBuiltRewardSupplierResponseMapper(RewardSupplierType type) {
        BuiltRewardSupplierResponseMapper mapper = mappersByType.get(type);
        if (mapper == null) {
            mapper = mappersByType.get(RewardSupplierType.CUSTOM_REWARD);
        }
        return mapper;
    }

}
