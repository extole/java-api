package com.extole.client.rest.impl.campaign.controller.action.fire.as.person.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@SuppressWarnings("rawtypes")
@Component
public class FireAsPersonIdentificationResponseMapperRepository {

    private final Map<FireAsPersonIdenticationType, FireAsPersonIdentificationResponseMapper> fireAsPersonMappers;

    @Autowired
    public FireAsPersonIdentificationResponseMapperRepository(List<FireAsPersonIdentificationResponseMapper> mappers) {
        this.fireAsPersonMappers = mappers.stream().collect(
            Collectors.toMap(item -> item.getType(), item -> item));
    }

    public FireAsPersonIdentificationResponseMapper
        getMapper(com.extole.model.entity.campaign.FireAsPersonIdenticationType type) {
        if (!fireAsPersonMappers.containsKey(type)) {
            throw new RuntimeException(
                "Response mapper for type " + type + " not found");
        }
        return fireAsPersonMappers.get(type);
    }
}
