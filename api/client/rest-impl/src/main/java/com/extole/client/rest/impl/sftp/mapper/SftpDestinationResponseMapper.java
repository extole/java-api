package com.extole.client.rest.impl.sftp.mapper;

import java.time.ZoneId;

import com.extole.client.rest.sftp.SftpDestinationResponse;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.model.entity.client.sftp.SftpDestination;

public interface SftpDestinationResponseMapper<I extends SftpDestination, O extends SftpDestinationResponse> {

    O toResponse(I sftpDestination, ZoneId timeZone);

    SftpDestinationType getType();
}
