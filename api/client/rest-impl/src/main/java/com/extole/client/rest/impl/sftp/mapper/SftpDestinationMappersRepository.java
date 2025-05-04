package com.extole.client.rest.impl.sftp.mapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.impl.sftp.SftpDestinationRestRuntimeException;
import com.extole.client.rest.sftp.SftpDestinationType;

@Component
@SuppressWarnings("rawtypes")
public class SftpDestinationMappersRepository {

    private final Map<SftpDestinationType, SftpDestinationCreateRequestMapper> createRequestMappers;
    private final Map<SftpDestinationType, SftpDestinationUpdateRequestMapper> updateRequestMappers;
    private final Map<SftpDestinationType, SftpDestinationResponseMapper> responseMappers;

    @Autowired
    public SftpDestinationMappersRepository(List<SftpDestinationCreateRequestMapper> createRequestMappersList,
        List<SftpDestinationUpdateRequestMapper> updateRequestMappersList,
        List<SftpDestinationResponseMapper> responseMappersList) {
        createRequestMappers = ImmutableMap.copyOf(createRequestMappersList.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity())));
        updateRequestMappers = ImmutableMap.copyOf(updateRequestMappersList.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity())));
        responseMappers = ImmutableMap.copyOf(responseMappersList.stream()
            .collect(Collectors.toMap(mapper -> mapper.getType(), Function.identity())));
    }

    public SftpDestinationCreateRequestMapper getCreateRequestMapper(SftpDestinationType type) {
        SftpDestinationCreateRequestMapper mapper = createRequestMappers.get(type);
        if (mapper == null) {
            throw new SftpDestinationRestRuntimeException(
                "Could not find sftp destination create request mapper for type: " + type);
        }
        return mapper;
    }

    public SftpDestinationUpdateRequestMapper getUpdateRequestMapper(SftpDestinationType type) {
        SftpDestinationUpdateRequestMapper mapper = updateRequestMappers.get(type);
        if (mapper == null) {
            throw new SftpDestinationRestRuntimeException(
                "Could not find sftp destination update request mapper for type: " + type);
        }
        return mapper;
    }

    public SftpDestinationResponseMapper getResponseMapper(SftpDestinationType type) {
        SftpDestinationResponseMapper mapper = responseMappers.get(type);
        if (mapper == null) {
            throw new SftpDestinationRestRuntimeException(
                "Could not find sftp destination response mapper for type: " + type);
        }
        return mapper;
    }
}
