package com.extole.common.rest.support.request.resolver;

import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public class MissingIdRequestTypeResolverException extends PolymorphicRequestTypeResolverException {

    private static final String MESSAGE_PATTERN =
        "Could not find id field: %s while trying to resolve polymorphic type for client: %s";

    private final String idFieldName;

    public MissingIdRequestTypeResolverException(String idFieldName, Id<ClientHandle> clientId) {
        super(String.format(MESSAGE_PATTERN, idFieldName, clientId));
        this.idFieldName = idFieldName;
    }

    public String getIdFieldName() {
        return idFieldName;
    }
}
