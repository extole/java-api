package com.extole.client.rest.impl.campaign.built.flow.step.metric;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepMetricResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepMetric;

@Component
public final class BuiltCampaignFlowStepMetricRestMapper {

    public BuiltCampaignFlowStepMetricResponse
        toBuiltFlowStepMetricResponse(BuiltCampaignFlowStepMetric flowStepMetric) {
        return new BuiltCampaignFlowStepMetricResponse(
            flowStepMetric.getId().getValue(),
            flowStepMetric.getName(),
            flowStepMetric.getDescription().orElse(null),
            flowStepMetric.getExpression(),
            flowStepMetric.getUnit(),
            flowStepMetric.getTags(),
            flowStepMetric.getComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            flowStepMetric.getComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

}
