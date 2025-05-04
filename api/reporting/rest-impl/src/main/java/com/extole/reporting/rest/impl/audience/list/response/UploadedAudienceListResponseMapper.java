package com.extole.reporting.rest.impl.audience.list.response;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.list.UploadedAudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.response.UploadedAudienceListResponse;

@Component
public class UploadedAudienceListResponseMapper implements
    AudienceListResponseMapper<UploadedAudienceListMappedResponse> {

    public UploadedAudienceListResponse toResponse(UploadedAudienceListMappedResponse audienceList, ZoneId timeZone) {
        return new UploadedAudienceListResponse(audienceList.getId().getValue(),
            audienceList.getName(),
            AudienceListState.valueOf(audienceList.getAudienceListState().getState().name()),
            audienceList.getTags(),
            audienceList.getFileAssetId().getValue(),
            audienceList.getAudienceId().map(Id::getValue),
            audienceList.getDescription(),
            audienceList.getEventColumns(),
            audienceList.getEventData(),
            audienceList.getMemberCount(),
            audienceList.getInputRowsCount(),
            audienceList.getAnonymousCount(),
            audienceList.getNonProcessedCount(),
            audienceList.getLastUpdatedDate().map(date -> date.atZone(timeZone)),
            audienceList.getAudienceListState().getErrorCode().map(Enum::name),
            audienceList.getAudienceListState().getErrorMessage());
    }

    @Override
    public AudienceListType getType() {
        return AudienceListType.UPLOADED;
    }
}
