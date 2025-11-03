package com.extole.client.rest.impl.campaign.controller.action.incentivize.status.update;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeStatusUpdateConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.incentivize.status.update.CampaignControllerActionIncentivizeStatusUpdateResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionIncentivizeStatusUpdate;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
final class CampaignControllerActionIncentivizeStatusUpdateResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionIncentivizeStatusUpdate,
        CampaignControllerActionIncentivizeStatusUpdateResponse,
        CampaignControllerActionIncentivizeStatusUpdateConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    CampaignControllerActionIncentivizeStatusUpdateResponseMapper(
        CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionIncentivizeStatusUpdateResponse toResponse(
        CampaignControllerActionIncentivizeStatusUpdate action, ZoneId timeZone) {

        return new CampaignControllerActionIncentivizeStatusUpdateResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            action.getLegacyActionId(),
            Evaluatables.remapNestedOptional(action.getEventType(), new TypeReference<>() {}),
            action.getPartnerEventId(),
            action.getReviewStatus(),
            action.getMessage(),
            action.isMoveToPending(),
            action.getData());
    }

    @Override
    public CampaignControllerActionIncentivizeStatusUpdateConfiguration toConfiguration(
        CampaignControllerActionIncentivizeStatusUpdate action,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {

        return new CampaignControllerActionIncentivizeStatusUpdateConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getLegacyActionId(),
            Evaluatables.remapNestedOptional(action.getEventType(), new TypeReference<>() {}),
            action.getPartnerEventId(),
            action.getReviewStatus(),
            action.getMessage(),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toUnmodifiableList()),
            action.isMoveToPending(),
            action.getData());
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE_STATUS_UPDATE;
    }

}
