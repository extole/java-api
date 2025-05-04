package com.extole.client.rest.impl.sftp.resolver;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.id.Id;
import com.extole.model.entity.client.sftp.SftpDestination;

public class SftpDestinationRequestResolverRestWrapperException
    extends PolymorphicRequestTypeResolverRestWrapperException {

    private static final String MESSAGE_PATTERN = "Could not find sftp destination by id: %s for client: %s";

    public SftpDestinationRequestResolverRestWrapperException(Id<ClientHandle> clientId,
        Id<SftpDestination> sftpDestinationId, ExtoleRestException cause) {
        super(String.format(MESSAGE_PATTERN, sftpDestinationId, clientId), cause);
    }
}
