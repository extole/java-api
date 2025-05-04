package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.incentivize.BuiltCampaignControllerActionIncentivizeResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.incentivize.IncentivizeActionOverrideType;
import com.extole.client.rest.campaign.controller.action.incentivize.IncentivizeActionType;
import com.extole.client.rest.campaign.controller.action.incentivize.ReviewStatus;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionIncentivize;

@Component
public class BuiltCampaignControllerActionIncentivizeResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionIncentivize,
        BuiltCampaignControllerActionIncentivizeResponse> {

    @Override
    public BuiltCampaignControllerActionIncentivizeResponse toResponse(BuiltCampaignControllerActionIncentivize action,
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
        return new BuiltCampaignControllerActionIncentivizeResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            IncentivizeActionType.valueOf(action.getIncentivizeActionType().name()),
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
            ReviewStatus.valueOf(action.getReviewStatus().name()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.INCENTIVIZE;
    }

}
