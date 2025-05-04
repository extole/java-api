package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.signal.v1.BuiltCampaignControllerActionSignalV1Response;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionSignalV1;

@Component
public class BuiltCampaignControllerActionSignalV1ResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionSignalV1,
        BuiltCampaignControllerActionSignalV1Response> {

    @Override
    public BuiltCampaignControllerActionSignalV1Response
        toResponse(BuiltCampaignControllerActionSignalV1 action, ZoneId timeZone) {
        return new BuiltCampaignControllerActionSignalV1Response(action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()), action.getSignalPollingId(),
            action.getData(),
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
        return CampaignControllerActionType.SIGNAL_V1;
    }

}
