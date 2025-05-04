package com.extole.client.rest.impl.campaign.flow.step.metric;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepMetricConfiguration;
import com.extole.client.rest.campaign.flow.step.metric.CampaignFlowStepMetricResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignFlowStepMetric;

@Component
public final class CampaignFlowStepMetricRestMapper {
    private final CampaignComponentRestMapper campaignComponentRestMapper;

    @Autowired
    CampaignFlowStepMetricRestMapper(CampaignComponentRestMapper campaignComponentRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
    }

    public CampaignFlowStepMetricResponse toFlowStepMetricResponse(CampaignFlowStepMetric flowStepMetric) {
        return new CampaignFlowStepMetricResponse(
            flowStepMetric.getId().getValue(),
            flowStepMetric.getName(),
            flowStepMetric.getDescription(),
            flowStepMetric.getExpression(),
            flowStepMetric.getUnit(),
            flowStepMetric.getTags(),
            flowStepMetric.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            flowStepMetric.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    public CampaignFlowStepMetricConfiguration toFlowStepMetricConfiguration(CampaignFlowStepMetric flowStepMetric,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignFlowStepMetricConfiguration(
            Omissible.of(Id.valueOf(flowStepMetric.getId().getValue())),
            flowStepMetric.getName(),
            flowStepMetric.getDescription(),
            flowStepMetric.getExpression(),
            flowStepMetric.getUnit(),
            flowStepMetric.getTags(),
            flowStepMetric.getCampaignComponentReferences()
                .stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    reference -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

}
