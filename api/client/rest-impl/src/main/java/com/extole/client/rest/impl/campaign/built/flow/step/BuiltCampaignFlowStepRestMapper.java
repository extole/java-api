package com.extole.client.rest.impl.campaign.built.flow.step;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepResponse;
import com.extole.client.rest.campaign.built.flow.step.BuiltCampaignFlowStepWordsResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.impl.campaign.built.flow.step.app.BuiltCampaignFlowStepAppRestMapper;
import com.extole.client.rest.impl.campaign.built.flow.step.metric.BuiltCampaignFlowStepMetricRestMapper;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStep;
import com.extole.model.entity.campaign.built.BuiltCampaignFlowStepWords;

@Component
public class BuiltCampaignFlowStepRestMapper {
    private final BuiltCampaignFlowStepMetricRestMapper builtCampaignFlowStepMetricRestMapper;
    private final BuiltCampaignFlowStepAppRestMapper builtCampaignFlowStepAppRestMapper;

    @Autowired
    public BuiltCampaignFlowStepRestMapper(BuiltCampaignFlowStepMetricRestMapper builtCampaignFlowStepMetricRestMapper,
        BuiltCampaignFlowStepAppRestMapper builtCampaignFlowStepAppRestMapper) {
        this.builtCampaignFlowStepMetricRestMapper = builtCampaignFlowStepMetricRestMapper;
        this.builtCampaignFlowStepAppRestMapper = builtCampaignFlowStepAppRestMapper;
    }

    public BuiltCampaignFlowStepResponse toBuiltFlowStepResponse(BuiltCampaignFlowStep flowStep) {

        return new BuiltCampaignFlowStepResponse(
            flowStep.getId().getValue(),
            flowStep.getFlowPath(),
            flowStep.getSequence(),
            flowStep.getStepName(),
            flowStep.getIconType(),
            flowStep.getMetrics().stream()
                .map(metric -> builtCampaignFlowStepMetricRestMapper.toBuiltFlowStepMetricResponse(metric))
                .collect(Collectors.toList()),
            flowStep.getApps().stream()
                .map(flowStepApp -> builtCampaignFlowStepAppRestMapper.toBuiltFlowStepAppResponse(flowStepApp))
                .collect(Collectors.toList()),
            flowStep.getTags(),
            flowStep.getName(),
            flowStep.getIconColor(),
            flowStep.getDescription(),
            toFlowStepWordsResponse(flowStep.getWords()),
            flowStep.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            flowStep.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    private BuiltCampaignFlowStepWordsResponse toFlowStepWordsResponse(BuiltCampaignFlowStepWords flowStepWords) {
        return new BuiltCampaignFlowStepWordsResponse(flowStepWords.getSingularNounName(),
            flowStepWords.getPluralNounName(), flowStepWords.getVerbName(), flowStepWords.getRateName(),
            flowStepWords.getPersonCountingName());
    }

}
