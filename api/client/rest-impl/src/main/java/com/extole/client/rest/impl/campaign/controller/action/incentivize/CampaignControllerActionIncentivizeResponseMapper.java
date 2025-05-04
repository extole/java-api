package com.extole.client.rest.impl.campaign.controller.action.incentivize;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionIncentivizeConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.incentivize.CampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.controller.action.incentivize.IncentivizeActionOverrideType;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionIncentivize;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionIncentivizeResponseMapper implements
    CampaignControllerActionResponseMapper<
        CampaignControllerActionIncentivize,
        CampaignControllerActionIncentivizeResponse,
        CampaignControllerActionIncentivizeConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionIncentivizeResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerActionIncentivizeResponse toResponse(CampaignControllerActionIncentivize action,
        ZoneId timeZone) {
        Map<IncentivizeActionOverrideType, String> overrides =
            action.getOverrides()
                .entrySet()
                .stream()
                .filter(
                    entry -> entry.getKey() != com.extole.model.entity.campaign.IncentivizeActionOverrideType.ACTION_ID)
                .collect(
                    Collectors.toMap(
                        entry -> IncentivizeActionOverrideType.valueOf(entry.getKey().name()), Map.Entry::getValue));
        return new CampaignControllerActionIncentivizeResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            Evaluatables.remapEnum(action.getIncentivizeActionType(), new TypeReference<>() {}),
            overrides,
            action.getActionName(),
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
                .collect(Collectors.toList()),
            Evaluatables.remapEnum(action.getReviewStatus(), new TypeReference<>() {}));
    }

    @Override
    public CampaignControllerActionIncentivizeConfiguration
        toConfiguration(CampaignControllerActionIncentivize action,
            ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        Map<com.extole.client.rest.campaign.configuration.IncentivizeActionOverrideType, String> overrides =
            action.getOverrides()
                .entrySet()
                .stream()
                .filter(
                    entry -> entry.getKey() != com.extole.model.entity.campaign.IncentivizeActionOverrideType.ACTION_ID)
                .collect(
                    Collectors.toMap(
                        entry -> com.extole.client.rest.campaign.configuration.IncentivizeActionOverrideType
                            .valueOf(entry.getKey().name()),
                        Map.Entry::getValue));
        return new CampaignControllerActionIncentivizeConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            Evaluatables.remapEnum(action.getIncentivizeActionType(), new TypeReference<>() {}),
            overrides,
            action.getActionName(),
            action.getData(),
            action.getEnabled(),
            action.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()),
            Evaluatables.remapEnum(action.getReviewStatus(), new TypeReference<>() {}));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE;
    }

}
