package com.extole.reporting.rest.impl.audience.list.response;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.list.AudienceListMappedResponse;
import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;
import com.extole.reporting.rest.audience.list.response.AudienceListDebugResponse;

@Component
public class AudienceListDebugResponseMapper {

    public AudienceListDebugResponse toResponse(AudienceListMappedResponse audienceList) {
        return new AudienceListDebugResponse(
            AudienceListType.valueOf(audienceList.getType().name()),
            audienceList.getId().getValue(),
            audienceList.getName(),
            AudienceListState.valueOf(audienceList.getAudienceListState().getState().name()),
            audienceList.getAudienceListState().getDebugMessage(),
            audienceList.getAudienceListState().getErrorCode().map(Enum::name),
            audienceList.getAudienceListState().getErrorMessage());
    }
}
