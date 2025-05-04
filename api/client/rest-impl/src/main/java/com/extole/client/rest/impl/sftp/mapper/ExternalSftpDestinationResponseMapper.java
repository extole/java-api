package com.extole.client.rest.impl.sftp.mapper;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.sftp.ExternalSftpDestinationResponse;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.model.entity.client.sftp.ExternalSftpDestination;

@Component
final class ExternalSftpDestinationResponseMapper
    implements SftpDestinationResponseMapper<ExternalSftpDestination, ExternalSftpDestinationResponse> {

    @Override
    public ExternalSftpDestinationResponse toResponse(ExternalSftpDestination sftpDestination, ZoneId timeZone) {
        return ExternalSftpDestinationResponse.builder()
            .withId(sftpDestination.getId().getValue())
            .withName(sftpDestination.getName())
            .withUsername(sftpDestination.getUsername())
            .withDropboxPath(sftpDestination.getDropboxPath())
            .withHost(sftpDestination.getHost())
            .withPort(sftpDestination.getPort())
            .withKeyId(sftpDestination.getKeyId().getValue())
            .withPartnerKeyId(sftpDestination.getPartnerKeyId().map(id -> id.getValue()).orElse(null))
            .withExtoleKeyId(sftpDestination.getExtoleKeyId().getValue())
            .withCreatedDate(sftpDestination.getCreatedDate().atZone(timeZone))
            .withUpdatedDate(sftpDestination.getUpdatedDate().atZone(timeZone))
            .build();
    }

    @Override
    public SftpDestinationType getType() {
        return SftpDestinationType.EXTERNAL;
    }
}
