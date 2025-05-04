package com.extole.client.rest.impl.sftp.mapper;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.sftp.LocalSftpDestinationResponse;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.model.entity.client.sftp.LocalSftpDestination;

@Component
final class LocalSftpDestinationResponseMapper
    implements SftpDestinationResponseMapper<LocalSftpDestination, LocalSftpDestinationResponse> {

    @Override
    public LocalSftpDestinationResponse toResponse(LocalSftpDestination sftpDestination, ZoneId timeZone) {
        return LocalSftpDestinationResponse.builder()
            .withId(sftpDestination.getId().getValue())
            .withName(sftpDestination.getName())
            .withUsername(sftpDestination.getUsername())
            .withFileProcessingEnabled(sftpDestination.isFileProcessingEnabled())
            .withDropboxPath(sftpDestination.getDropboxPath())
            .withHost(sftpDestination.getHost())
            .withPort(sftpDestination.getPort())
            .withKeyIds(sftpDestination.getKeyIds().stream().map(key -> key.getValue()).collect(Collectors.toSet()))
            .withPartnerKeyId(sftpDestination.getPartnerKeyId().map(id -> id.getValue()).orElse(null))
            .withExtoleKeyId(sftpDestination.getExtoleKeyId().getValue())
            .withCreatedDate(sftpDestination.getCreatedDate().atZone(timeZone))
            .withUpdatedDate(sftpDestination.getUpdatedDate().atZone(timeZone))
            .build();
    }

    @Override
    public SftpDestinationType getType() {
        return SftpDestinationType.LOCAL;
    }
}
