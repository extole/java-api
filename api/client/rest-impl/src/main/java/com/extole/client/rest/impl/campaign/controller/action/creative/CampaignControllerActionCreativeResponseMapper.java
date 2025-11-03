package com.extole.client.rest.impl.campaign.controller.action.creative;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toUnmodifiableList;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerActionCreativeConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.creative.CampaignControllerActionCreativeResponse;
import com.extole.client.rest.campaign.controller.action.creative.Classification;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.action.CampaignControllerActionResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerActionCreative;
import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionCreativeResponseMapper implements
    CampaignControllerActionResponseMapper<CampaignControllerActionCreative, CampaignControllerActionCreativeResponse,
        CampaignControllerActionCreativeConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerActionCreativeResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    public CampaignControllerActionCreativeResponse toResponse(CampaignControllerActionCreative action,
        Map<String, List<String>> logMessages, ZoneId timeZone) {
        return new CampaignControllerActionCreativeResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            Classification.valueOf(action.getClassification().name()),
            action.getApiVersion().getVersion(),
            action.getThemeVersion().orElse(StringUtils.EMPTY),
            action.getCreativeArchiveId().isPresent(),
            action.getCreativeArchiveId().map(value -> value.getVersion()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(toUnmodifiableList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            logMessages);
    }

    @Override
    public CampaignControllerActionCreativeResponse
        toResponse(CampaignControllerActionCreative action, ZoneId timeZone) {
        return new CampaignControllerActionCreativeResponse(
            action.getId().getValue(),
            CampaignControllerActionQuality.valueOf(action.getQuality().name()),
            Classification.valueOf(action.getClassification().name()),
            action.getApiVersion().getVersion(),
            action.getThemeVersion().orElse(StringUtils.EMPTY),
            action.getCreativeArchiveId().isPresent(),
            action.getCreativeArchiveId().map(value -> value.getVersion()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(toUnmodifiableList()),
            action.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()),
            emptyMap());
    }

    @Override
    public CampaignControllerActionCreativeConfiguration
        toConfiguration(CampaignControllerActionCreative action, ZoneId timeZone,
            Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerActionCreativeConfiguration(
            Omissible.of(Id.valueOf(action.getId().getValue())),
            com.extole.client.rest.campaign.configuration.CampaignControllerActionQuality
                .valueOf(action.getQuality().name()),
            action.getCreativeArchiveId().map(value -> value.getId().getValue()),
            action.getApiVersion().getVersion(),
            Classification.valueOf(action.getClassification().name()),
            action.getThemeVersion().orElse(StringUtils.EMPTY),
            action.getCreativeArchiveId().map(value -> value.getVersion()),
            action.getEnabled(),
            action.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(toUnmodifiableList()));
    }

    @Override
    public CampaignControllerActionType getActionType() {
        return CampaignControllerActionType.CREATIVE;
    }

}
