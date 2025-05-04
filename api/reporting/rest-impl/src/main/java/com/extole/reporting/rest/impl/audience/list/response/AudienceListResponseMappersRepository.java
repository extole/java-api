package com.extole.reporting.rest.impl.audience.list.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.list.AudienceList;
import com.extole.reporting.rest.audience.list.AudienceListType;

@Component
public class AudienceListResponseMappersRepository {

    private final Map<AudienceListType, AudienceListResponseMapper<? extends AudienceList>> responseMappers;

    @Autowired
    public AudienceListResponseMappersRepository(
        List<AudienceListResponseMapper<? extends AudienceList>> responseMappers) {
        this.responseMappers = responseMappers.stream()
            .collect(Collectors.toMap(item -> item.getType(), item -> item));
    }

    @SuppressWarnings("rawtypes")
    public AudienceListResponseMapper getMapper(AudienceListType type) {
        if (!responseMappers.containsKey(type)) {
            throw new RuntimeException("Mapper of type=" + type + " not found");
        }

        return responseMappers.get(type);
    }
}
