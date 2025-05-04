package com.extole.reporting.rest.impl.audience.list.request;

import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.AudienceList;

public class AudienceListRequestResolverRestWrapperException
    extends PolymorphicRequestTypeResolverRestWrapperException {

    private static final String MESSAGE_PATTERN = "Could not find audience list by ID: %s for client: %s.";

    public AudienceListRequestResolverRestWrapperException(Id<AudienceList> audienceListId, Id<ClientHandle> clientId,
        ExtoleRestException cause) {
        super(String.format(MESSAGE_PATTERN, audienceListId, clientId), cause);
    }
}
