package com.extole.client.rest.impl.security.key.built;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.client.security.key.ClientKey;

@Component
@SuppressWarnings("rawtypes")
public class BuiltClientKeyResponseMapperRepository {

    private final Map<ClientKey.Algorithm, BuiltClientKeyResponseMapper> mappersByAlgorithm;

    @Autowired
    public BuiltClientKeyResponseMapperRepository(List<BuiltClientKeyResponseMapper> mappers) {
        Map<ClientKey.Algorithm, BuiltClientKeyResponseMapper> mappersMap = new HashMap<>();
        for (BuiltClientKeyResponseMapper mapper : mappers) {
            if (mappersMap.containsKey(mapper.getAlgorithm())) {
                throw new RuntimeException(
                    "Found multiple instances of ClientKeyResponseMapper for the same algorithm: "
                        + mapper.getAlgorithm());
            }
            mappersMap.put(mapper.getAlgorithm(), mapper);
        }
        this.mappersByAlgorithm = Collections.unmodifiableMap(mappersMap);
    }

    public BuiltClientKeyResponseMapper getClientKeyResponseMapper(ClientKey.Algorithm algorithm) {
        BuiltClientKeyResponseMapper mapper = mappersByAlgorithm.get(algorithm);
        if (mapper == null) {
            mapper = mappersByAlgorithm.get(ClientKey.Algorithm.GENERIC);
        }
        return mapper;
    }

}
