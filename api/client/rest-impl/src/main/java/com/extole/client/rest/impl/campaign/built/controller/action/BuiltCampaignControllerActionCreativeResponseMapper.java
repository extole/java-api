package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.controller.action.creative.BuiltCampaignControllerActionCreativeResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.creative.Classification;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerActionCreative;
import com.extole.model.entity.campaign.built.BuiltCreativeArchiveId;

@Component
public class BuiltCampaignControllerActionCreativeResponseMapper implements
    BuiltCampaignControllerActionResponseMapper<
        BuiltCampaignControllerActionCreative, BuiltCampaignControllerActionCreativeResponse> {

    @Override
    public BuiltCampaignControllerActionCreativeResponse toResponse(BuiltCampaignControllerActionCreative action,
        ZoneId timeZone) {
        return new BuiltCampaignControllerActionCreativeResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            action.getCreativeArchiveId().flatMap(BuiltCreativeArchiveId::getBuildVersion).orElse(null),
            Classification.valueOf(action.getClassification().name()),
            action.getApiVersion().getVersion(),
            action.getThemeVersion().orElse(StringUtils.EMPTY),
            action.getCreativeArchiveId().isPresent(),
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
            action.getCreativeArchiveId().map(value -> value.getVersion()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.CREATIVE;
    }

}
