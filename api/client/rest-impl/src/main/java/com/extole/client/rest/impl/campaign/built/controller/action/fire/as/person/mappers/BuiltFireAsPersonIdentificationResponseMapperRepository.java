package com.extole.client.rest.impl.campaign.built.controller.action.fire.as.person.mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.FireAsPersonIdenticationType;

@SuppressWarnings({"rawtypes"})
@Component
public class BuiltFireAsPersonIdentificationResponseMapperRepository {

    private final Map<FireAsPersonIdenticationType, BuiltFireAsPersonIdentificationResponseMapper> fireAsPersonMappers;

    public BuiltFireAsPersonIdentificationResponseMapperRepository(
        List<BuiltFireAsPersonIdentificationResponseMapper> mappers) {
        this.fireAsPersonMappers = mappers.stream().collect(
            Collectors.toMap(item -> item.getType(), item -> item));
    }

    public BuiltFireAsPersonIdentificationResponseMapper getMapper(FireAsPersonIdenticationType type) {
        if (!fireAsPersonMappers.containsKey(type)) {
            throw new RuntimeException(
                "Response mapper for type " + type + " not found");
        }
        return fireAsPersonMappers.get(type);
    }
}
