package com.extole.reporting.rest.impl.audience.list.request;

import com.extole.authorization.service.Authorization;
import com.extole.reporting.entity.report.audience.list.AudienceList;
import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.AudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.DynamicAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.StaticAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.UploadedAudienceListValidationRestException;
import com.extole.reporting.rest.audience.list.request.AudienceListRequest;
import com.extole.reporting.service.audience.list.AudienceListBuilder;

public interface AudienceListRequestHandler<REQUEST extends AudienceListRequest,
    BUILDER extends AudienceListBuilder<? extends AudienceList>> {

    AudienceListMappedResponse upload(Authorization authorization, REQUEST request, BUILDER builder)
        throws AudienceListValidationRestException, DynamicAudienceListValidationRestException,
        StaticAudienceListValidationRestException, UploadedAudienceListValidationRestException;

    AudienceListType getType();
}
