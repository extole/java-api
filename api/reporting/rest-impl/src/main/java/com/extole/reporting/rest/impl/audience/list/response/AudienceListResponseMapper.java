package com.extole.reporting.rest.impl.audience.list.response;

import java.time.ZoneId;

import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.response.AudienceListResponse;

public interface AudienceListResponseMapper<AUDIENCE_LIST extends AudienceListMappedResponse> {

    AudienceListResponse toResponse(AUDIENCE_LIST audienceList, ZoneId timeZone);

    AudienceListType getType();
}
