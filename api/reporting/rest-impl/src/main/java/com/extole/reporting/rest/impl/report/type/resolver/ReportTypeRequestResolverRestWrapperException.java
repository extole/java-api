package com.extole.reporting.rest.impl.report.type.resolver;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.id.Id;

public class ReportTypeRequestResolverRestWrapperException extends PolymorphicRequestTypeResolverRestWrapperException {

    private static final String MESSAGE_PATTERN = "Could not get report type by name: %s for client: %s.";

    public ReportTypeRequestResolverRestWrapperException(String name, Id<ClientHandle> clientId,
        ExtoleRestException cause) {
        super(String.format(MESSAGE_PATTERN, name, clientId), cause);
    }
}
