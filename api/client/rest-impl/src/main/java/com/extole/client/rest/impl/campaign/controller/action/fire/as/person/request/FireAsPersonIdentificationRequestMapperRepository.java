package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.action.fire.as.person.FireAsPersonIdenticationType;

@SuppressWarnings("rawtypes")
@Component
public class FireAsPersonIdentificationRequestMapperRepository {

    private final Map<FireAsPersonIdenticationType, FireAsPersonIdentificationRequestMapper> fireAsPersonMappers;

    public FireAsPersonIdentificationRequestMapperRepository(
        List<FireAsPersonIdentificationRequestMapper> mappers) {
        this.fireAsPersonMappers = mappers.stream().collect(
            Collectors.toMap(item -> item.getType(), item -> item));
    }

    public FireAsPersonIdentificationRequestMapper getMapper(FireAsPersonIdenticationType type) {
        if (!fireAsPersonMappers.containsKey(type)) {
            throw new RuntimeException(
                "Response mapper for type " + type + " not found");
        }
        return fireAsPersonMappers.get(type);
    }

}
