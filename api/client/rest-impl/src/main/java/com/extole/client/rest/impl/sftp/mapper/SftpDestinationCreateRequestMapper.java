package com.extole.client.rest.impl.sftp.mapper;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.SftpDestinationCreateRequest;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.model.entity.client.sftp.SftpDestination;

public interface SftpDestinationCreateRequestMapper<
    I extends SftpDestinationCreateRequest,
    O extends SftpDestination> {

    O create(Authorization authorization, I createRequest) throws AuthorizationException,
        SftpDestinationValidationRestException;

    SftpDestinationType getType();
}
