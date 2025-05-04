package com.extole.client.rest.impl.sftp.mapper;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.sftp.SftpDestinationRestException;
import com.extole.client.rest.sftp.SftpDestinationType;
import com.extole.client.rest.sftp.SftpDestinationUpdateRequest;
import com.extole.client.rest.sftp.SftpDestinationValidationRestException;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.SftpDestination;

public interface SftpDestinationUpdateRequestMapper<
    I extends SftpDestinationUpdateRequest,
    O extends SftpDestination> {

    O update(Authorization authorization, Id<SftpDestination> sftpDestinationId, I updateRequest)
        throws AuthorizationException, SftpDestinationRestException, SftpDestinationValidationRestException;

    SftpDestinationType getType();
}
