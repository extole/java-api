package com.extole.reporting.rest.impl.audience.list.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.list.AudienceList;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.request.AudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListBuilder;

@Component
public class AudienceListRequestMappersRepository {

    private final Map<AudienceListType, AudienceListRequestHandler<? extends AudienceListRequest,
        ? extends AudienceListBuilder<? extends AudienceList>>> uploaders;

    @Autowired
    public AudienceListRequestMappersRepository(
        List<AudienceListRequestHandler<? extends AudienceListRequest,
            ? extends AudienceListBuilder<? extends AudienceList>>> mappers) {
        this.uploaders =
            mappers.stream().collect(
                Collectors.toMap(item -> item.getType(), item -> item));
    }

    @SuppressWarnings("rawtypes")
    public AudienceListRequestHandler getUploader(AudienceListType type) {
        if (!uploaders.containsKey(type)) {
            throw new RuntimeException("Request mapper of type=" + type + " not found");
        }

        return uploaders.get(type);
    }
}
