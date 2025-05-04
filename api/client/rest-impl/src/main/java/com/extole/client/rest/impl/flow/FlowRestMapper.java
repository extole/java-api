package com.extole.client.rest.impl.flow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.step.data.StepDataKeyType;
import com.extole.client.rest.campaign.step.data.StepDataPersistType;
import com.extole.client.rest.campaign.step.data.StepDataScope;
import com.extole.client.rest.flow.FlowStepDataResponse;
import com.extole.client.rest.flow.FlowStepMetricResponse;
import com.extole.client.rest.flow.FlowStepResponse;
import com.extole.client.rest.flow.FlowStepTriggerType;
import com.extole.client.rest.flow.FlowStepWordsResponse;
import com.extole.client.rest.flow.InputFlowStepTriggerResponse;
import com.extole.model.entity.campaign.built.BuiltStepData;
import com.extole.model.entity.flow.InputFlowStepTrigger;
import com.extole.model.shared.flow.FlowStep;
import com.extole.model.shared.flow.FlowStepMetric;

@Component
public class FlowRestMapper {

    @Autowired
    public FlowRestMapper() {
    }

    public FlowStepResponse toFlowStepResponse(FlowStep flowStep) {
        String flowPath = flowStep.getFlowPath();
        BigDecimal sequence = flowStep.getSequence();
        String stepName = flowStep.getStepName();
        String iconType = flowStep.getIconType();
        Set<String> tags = flowStep.getTags();
        String name = flowStep.getName();
        String iconColor = flowStep.getIconColor();
        Optional<String> description = flowStep.getDescription();

        FlowStepWordsResponse words = new FlowStepWordsResponse(
            flowStep.getWords().getSingularNounName(),
            flowStep.getWords().getPluralNounName(),
            flowStep.getWords().getVerbName(),
            flowStep.getWords().getRateName(),
            flowStep.getWords().getPersonCountingName());

        List<FlowStepMetricResponse> metrics = flowStep.getMetrics().stream()
            .map(this::toFlowStepMetricResponse)
            .collect(Collectors.toList());

        List<FlowStepDataResponse> data = flowStep.getData().stream()
            .map(dataValue -> toBuiltStepDataResponse(dataValue))
            .collect(Collectors.toList());

        return new FlowStepResponse(flowPath, sequence, stepName, iconType, metrics, tags, name, iconColor, data,
            flowStep.getTriggers().stream()
                .map(item -> toInputFlowStepTriggerResponse(item))
                .collect(Collectors.toSet()),
            description, words);
    }

    private InputFlowStepTriggerResponse toInputFlowStepTriggerResponse(InputFlowStepTrigger trigger) {
        return new InputFlowStepTriggerResponse(FlowStepTriggerType.valueOf(trigger.getType().name()),
            trigger.getEventNames());
    }

    private FlowStepMetricResponse toFlowStepMetricResponse(FlowStepMetric flowStepMetric) {
        String name = flowStepMetric.getName();
        String description = flowStepMetric.getDescription().orElse(null);
        String expression = flowStepMetric.getExpression();
        String unit = flowStepMetric.getUnit();
        Set<String> tags = flowStepMetric.getTags();

        return new FlowStepMetricResponse(name, description, expression, unit, tags);
    }

    private FlowStepDataResponse toBuiltStepDataResponse(BuiltStepData dataValue) {
        return new FlowStepDataResponse(
            dataValue.getName(),
            dataValue.getValue(),
            StepDataScope.valueOf(dataValue.getScope().name()),
            Boolean.valueOf(dataValue.isDimension()),
            dataValue.getPersistTypes().stream()
                .map(persistType -> StepDataPersistType.valueOf(persistType.name()))
                .collect(Collectors.toList()),
            dataValue.getDefaultValue(),
            StepDataKeyType.valueOf(dataValue.getKeyType().name()),
            dataValue.getEnabled());
    }

}
