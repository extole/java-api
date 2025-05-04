package com.extole.reporting.rest.impl.audience.list.response;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.list.DynamicAudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.response.DynamicAudienceListResponse;

@Component
public class DynamicAudienceListResponseMapper
    implements AudienceListResponseMapper<DynamicAudienceListMappedResponse> {

    public DynamicAudienceListResponse toResponse(DynamicAudienceListMappedResponse audienceList, ZoneId timeZone) {
        return new DynamicAudienceListResponse(audienceList.getId().getValue(),
            audienceList.getName(),
            AudienceListState.valueOf(audienceList.getAudienceListState().getState().name()),
            audienceList.getTags(),
            audienceList.getReportRunnerId().getValue(),
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
        return AudienceListType.DYNAMIC;
    }
}
