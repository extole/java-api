package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.schedule.BuiltCampaignControllerActionScheduleResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionSchedule;

@Component
public class BuiltCampaignControllerActionScheduleResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionSchedule,
        BuiltCampaignControllerActionScheduleResponse> {

    @Override
    public BuiltCampaignControllerActionScheduleResponse toResponse(BuiltCampaignControllerActionSchedule action,
        ZoneId timeZone) {
        List<ZonedDateTime> dates = action.getScheduleDates().stream()
            .map(date -> date.atZone(timeZone))
            .collect(Collectors.toList());

        return new BuiltCampaignControllerActionScheduleResponse(action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()), action.getScheduleName(),
            action.getScheduleDelays(), dates, action.isForce(), action.getData(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.SCHEDULE;
    }

}
