package com.extole.reporting.rest.impl.report.runner.resolver;

import com.extole.api.report_runner.ReportRunner;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.support.request.resolver.PolymorphicRequestTypeResolverRestWrapperException;
import com.extole.id.Id;

public class ReportRunnerRequestResolverRestWrapperException
    extends PolymorphicRequestTypeResolverRestWrapperException {

    private static final String MESSAGE_PATTERN = "Could not get report runner by id: %s for client: %s.";

    public ReportRunnerRequestResolverRestWrapperException(Id<ClientHandle> clientId,
        Id<ReportRunner> reportRunnerId, ExtoleRestException cause) {
        super(String.format(MESSAGE_PATTERN, reportRunnerId, clientId), cause);
    }
}
