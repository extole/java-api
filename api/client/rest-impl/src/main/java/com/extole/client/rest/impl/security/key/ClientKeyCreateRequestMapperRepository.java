package com.extole.client.rest.impl.security.key;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.security.key.ClientKeyAlgorithm;

@Component
@SuppressWarnings("rawtypes")
public class ClientKeyCreateRequestMapperRepository {

    private final Map<ClientKeyAlgorithm, ClientKeyCreateRequestMapper> mappersByAlgorithm;

    @Autowired
    public ClientKeyCreateRequestMapperRepository(List<ClientKeyCreateRequestMapper> mappers) {
        Map<ClientKeyAlgorithm, ClientKeyCreateRequestMapper> mappersMap = new HashMap<>();
        for (ClientKeyCreateRequestMapper mapper : mappers) {
            if (mappersMap.containsKey(mapper.getAlgorithm())) {
                throw new IllegalStateException(
                    "Found multiple instances of ClientKeyCreateRequestMapper for the same algorithm: "
                        + mapper.getAlgorithm());
            }
            mappersMap.put(mapper.getAlgorithm(), mapper);
        }
        this.mappersByAlgorithm = Collections.unmodifiableMap(mappersMap);
    }

    public ClientKeyCreateRequestMapper getClientKeyCreateRequestMapper(ClientKeyAlgorithm algorithm) {
        ClientKeyCreateRequestMapper mapper = mappersByAlgorithm.get(algorithm);
        if (mapper == null) {
            mapper = mappersByAlgorithm.get(ClientKeyAlgorithm.GENERIC);
        }
        return mapper;
    }

}
