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
public class ClientKeyUpdateRequestMapperRepository {

    private final Map<ClientKeyAlgorithm, ClientKeyUpdateRequestMapper> mappersByAlgorithm;

    @Autowired
    public ClientKeyUpdateRequestMapperRepository(List<ClientKeyUpdateRequestMapper> mappers) {
        Map<ClientKeyAlgorithm, ClientKeyUpdateRequestMapper> mappersMap = new HashMap<>();
        for (ClientKeyUpdateRequestMapper mapper : mappers) {
            if (mappersMap.containsKey(mapper.getAlgorithm())) {
                throw new IllegalStateException(
                    "Found multiple instances of ClientKeyUpdateRequestMapper for the same algorithm: "
                        + mapper.getAlgorithm());
            }
            mappersMap.put(mapper.getAlgorithm(), mapper);
        }
        this.mappersByAlgorithm = Collections.unmodifiableMap(mappersMap);
    }

    public ClientKeyUpdateRequestMapper getClientKeyUpdateRequestMapper(ClientKeyAlgorithm algorithm) {
        ClientKeyUpdateRequestMapper mapper = mappersByAlgorithm.get(algorithm);
        if (mapper == null) {
            mapper = mappersByAlgorithm.get(ClientKeyAlgorithm.GENERIC);
        }
        return mapper;
    }

}
