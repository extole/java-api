package com.extole.client.rest.impl.campaign.controller.action.approve;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionApproveConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.approve.CampaignControllerActionApproveResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionApprove;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionApproveResponseMapper implements
    CampaignControllerActionResponseMapper<
        CampaignControllerActionApprove,
        CampaignControllerActionApproveResponse,
        CampaignControllerActionConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionApproveResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionApproveResponse toResponse(CampaignControllerActionApprove action, ZoneId timeZone) {
        return new CampaignControllerActionApproveResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
            action.getForce(),
            action.getNote(),
            action.getCauseType(),
            action.getPollingId(),
            action.getPollingName(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getRewardTags());
    }

    @Override
    public CampaignControllerActionApproveConfiguration toConfiguration(CampaignControllerActionApprove action,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionApproveConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            action.getPartnerEventId(),
            action.getEventType(),
            action.getForce(),
            action.getNote(),
            action.getCauseType(),
            action.getPollingId(),
            action.getPollingName(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            action.getRewardTags());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.APPROVE;
    }

}
