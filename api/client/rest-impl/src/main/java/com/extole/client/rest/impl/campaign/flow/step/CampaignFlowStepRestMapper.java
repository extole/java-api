package com.extole.client.rest.impl.campaign.flow.step;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignFlowStepWordsConfiguration;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepResponse;
import com.extole.client.rest.campaign.flow.step.CampaignFlowStepWordsResponse;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapper;
import com.extole.client.rest.impl.campaign.flow.step.app.CampaignFlowStepAppRestMapper;
import com.extole.client.rest.impl.campaign.flow.step.metric.CampaignFlowStepMetricRestMapper;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignFlowStep;
import com.extole.model.entity.campaign.CampaignFlowStepWords;

@Component
public final class CampaignFlowStepRestMapper {
    private final CampaignComponentRestMapper campaignComponentRestMapper;
    private final CampaignFlowStepMetricRestMapper campaignFlowStepMetricRestMapper;
    private final CampaignFlowStepAppRestMapper campaignFlowStepAppRestMapper;

    @Autowired
    public CampaignFlowStepRestMapper(CampaignComponentRestMapper campaignComponentRestMapper,
        CampaignFlowStepMetricRestMapper campaignFlowStepMetricRestMapper,
        CampaignFlowStepAppRestMapper campaignFlowStepAppRestMapper) {
        this.campaignComponentRestMapper = campaignComponentRestMapper;
        this.campaignFlowStepMetricRestMapper = campaignFlowStepMetricRestMapper;
        this.campaignFlowStepAppRestMapper = campaignFlowStepAppRestMapper;
    }

    public CampaignFlowStepResponse toFlowStepResponse(CampaignFlowStep flowStep) {
        return new CampaignFlowStepResponse(flowStep.getId().getValue(),
            flowStep.getFlowPath(),
            flowStep.getSequence(),
            flowStep.getStepName(),
            flowStep.getIconType(),
            flowStep.getMetrics().stream()
                .map(flowStepMetric -> campaignFlowStepMetricRestMapper.toFlowStepMetricResponse(flowStepMetric))
                .collect(Collectors.toList()),
            flowStep.getApps().stream()
                .map(flowStepApp -> campaignFlowStepAppRestMapper.toFlowStepAppResponse(flowStepApp))
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

    public CampaignFlowStepConfiguration toFlowStepConfiguration(CampaignFlowStep flowStep,
        Map<Id<CampaignComponent>, String> componentNames) {
        return new CampaignFlowStepConfiguration(Omissible.of(Id.valueOf(flowStep.getId().getValue())),
            flowStep.getFlowPath(),
            flowStep.getSequence(),
            flowStep.getStepName(),
            flowStep.getIconType(),
            flowStep.getMetrics().stream()
                .map(flowStepMetric -> campaignFlowStepMetricRestMapper.toFlowStepMetricConfiguration(flowStepMetric,
                    componentNames))
                .collect(Collectors.toList()),
            flowStep.getApps().stream()
                .map(flowStepApp -> campaignFlowStepAppRestMapper.toFlowStepAppConfiguration(flowStepApp,
                    componentNames))
                .collect(Collectors.toList()),
            flowStep.getTags(),
            flowStep.getName(),
            flowStep.getIconColor(),
            flowStep.getDescription(),
            toFlowStepWordsConfiguration(flowStep.getWords()),
            flowStep.getCampaignComponentReferences().stream()
                .map(componentReference -> campaignComponentRestMapper.toComponentReferenceConfiguration(
                    componentReference,
                    (reference) -> componentNames.get(reference.getComponentId())))
                .collect(Collectors.toList()));
    }

    private CampaignFlowStepWordsResponse toFlowStepWordsResponse(CampaignFlowStepWords flowStepWords) {
        return new CampaignFlowStepWordsResponse(flowStepWords.getSingularNounName(), flowStepWords.getPluralNounName(),
            flowStepWords.getVerbName(), flowStepWords.getRateName(), flowStepWords.getPersonCountingName());
    }

    private CampaignFlowStepWordsConfiguration toFlowStepWordsConfiguration(CampaignFlowStepWords flowStepWords) {
        return new CampaignFlowStepWordsConfiguration(flowStepWords.getSingularNounName(),
            flowStepWords.getPluralNounName(),
            flowStepWords.getVerbName(), flowStepWords.getRateName(), flowStepWords.getPersonCountingName());
    }

}
