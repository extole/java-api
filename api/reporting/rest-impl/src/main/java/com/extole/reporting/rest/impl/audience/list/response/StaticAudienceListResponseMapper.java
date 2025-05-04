package com.extole.reporting.rest.impl.audience.list.response;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.list.StaticAudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.response.StaticAudienceListResponse;

@Component
public class StaticAudienceListResponseMapper implements AudienceListResponseMapper<StaticAudienceListMappedResponse> {

    public StaticAudienceListResponse toResponse(StaticAudienceListMappedResponse audienceList,
        ZoneId timeZone) {
        return new StaticAudienceListResponse(audienceList.getId().getValue(),
            audienceList.getName(),
            AudienceListState.valueOf(audienceList.getAudienceListState().getState().name()),
            audienceList.getTags(),
            audienceList.getReportId().getValue(),
            audienceList.getDescription(),
            audienceList.getEventColumns(),
            audienceList.getEventData(),
            audienceList.getMemberCount(),
            audienceList.getLastUpdatedDate().map(date -> date.atZone(timeZone)),
            audienceList.getAudienceListState().getErrorCode().map(Enum::name),
            audienceList.getAudienceListState().getErrorMessage());
    }

    @Override
    public AudienceListType getType() {
        return AudienceListType.STATIC;
    }
}
