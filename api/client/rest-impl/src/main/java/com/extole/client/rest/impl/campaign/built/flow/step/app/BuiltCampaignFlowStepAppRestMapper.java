package com.extole.client.rest.impl.campaign.built.flow.step.app;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepAppResponse;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepAppTypeResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepApp;

@Component
public final class BuiltCampaignFlowStepAppRestMapper {

    public BuiltCampaignFlowStepAppResponse toBuiltFlowStepAppResponse(BuiltCampaignFlowStepApp flowStepApp) {
        return new BuiltCampaignFlowStepAppResponse(
            flowStepApp.getName(),
            flowStepApp.getDescription(),
            new BuiltCampaignFlowStepAppTypeResponse(flowStepApp.getType().getName()),
            flowStepApp.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            flowStepApp.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

}
