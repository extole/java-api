package com.extole.client.rest.impl.campaign.controller.trigger.expression;

import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerExpressionConfiguration;
import com.extole.client.rest.campaign.controller.trigger.expression.CampaignControllerTriggerExpressionResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.controller.trigger.CampaignControllerTriggerResponseMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTriggerExpression;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerExpressionResponseMapper implements
    CampaignControllerTriggerResponseMapper<CampaignControllerTriggerExpression,
        CampaignControllerTriggerExpressionResponse, CampaignControllerTriggerExpressionConfiguration> {

    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    public CampaignControllerTriggerExpressionResponseMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    @Override
    public CampaignControllerTriggerExpressionResponse toResponse(CampaignControllerTriggerExpression trigger,
        ZoneId timeZone) {
        return new CampaignControllerTriggerExpressionResponse(trigger.getId().getValue(),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getData(),
            trigger.getExpression(),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            trigger.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerExpressionConfiguration toConfiguration(CampaignControllerTriggerExpression trigger,
        ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignControllerTriggerExpressionConfiguration(
            Omissible.of(Id.valueOf(trigger.getId().getValue())),
            Evaluatables.remapEnum(trigger.getPhase(), new TypeReference<>() {}),
            trigger.getName(),
            trigger.getParentTriggerGroupName(),
            trigger.getDescription(),
            trigger.getEnabled(),
            trigger.getNegated(),
            trigger.getData(),
            trigger.getExpression(),
            trigger.getComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    @Override
    public CampaignControllerTriggerType getTriggerType() {
        return CampaignControllerTriggerType.EXPRESSION;
    }

}
