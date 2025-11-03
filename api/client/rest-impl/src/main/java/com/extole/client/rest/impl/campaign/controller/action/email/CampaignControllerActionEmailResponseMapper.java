package com.extole.client.rest.impl.campaign.controller.action.email;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionEmailConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.email.CampaignControllerActionEmailResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionEmail;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionEmailResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionEmail, CampaignControllerActionEmailResponse,
        CampaignControllerActionEmailConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionEmailResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionEmailResponse toResponse(CampaignControllerActionEmail action, ZoneId timeZone) {
        return new CampaignControllerActionEmailResponse(action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()), action.getZoneName(),
            action.getData(),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionEmailConfiguration toConfiguration(CampaignControllerActionEmail action,
        ZoneId timeZone,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionEmailConfiguration(Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getZoneName(),
            action.getData(),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.EMAIL;
    }

}
