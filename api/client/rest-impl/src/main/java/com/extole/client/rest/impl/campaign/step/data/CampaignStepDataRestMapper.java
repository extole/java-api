package com.extole.client.rest.impl.campaign.step.data;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.step.data.BuiltStepDataResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.step.data.StepDataKeyType;
import com.extole.client.rest.campaign.step.data.StepDataPersistType;
import com.extole.client.rest.campaign.step.data.StepDataResponse;
import com.extole.client.rest.campaign.step.data.StepDataScope;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.built.BuiltStepData;

@Component
public class CampaignStepDataRestMapper {

    public List<StepDataResponse> toStepDataResponses(List<StepData> stepDataValues) {

        return stepDataValues.stream().map(value -> toStepDataResponse(value))
            .collect(Collectors.toUnmodifiableList());
    }

    public StepDataResponse toStepDataResponse(StepData stepDataValue) {
        return new StepDataResponse(
            stepDataValue.getId().getValue(),
            stepDataValue.getName(),
            stepDataValue.getValue(),
            Evaluatables.remapEnum(stepDataValue.getScope(), new TypeReference<>() {}),
            stepDataValue.isDimension(),
            Evaluatables.remapEnumCollection(stepDataValue.getPersistTypes(), new TypeReference<>() {}),
            stepDataValue.getDefaultValue(),
            Evaluatables.remapEnum(stepDataValue.getKeyType(), new TypeReference<>() {}),
            stepDataValue.getEnabled(),
            stepDataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            stepDataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

    public List<BuiltStepDataResponse> toBuiltStepDataResponses(List<BuiltStepData> builtStepDataValues) {
        return builtStepDataValues.stream().map(value -> toBuiltStepDataResponse(value))
            .collect(Collectors.toUnmodifiableList());
    }

    public BuiltStepDataResponse toBuiltStepDataResponse(BuiltStepData dataValue) {
        return new BuiltStepDataResponse(
            dataValue.getName(),
            dataValue.getValue(),
            StepDataScope.valueOf(dataValue.getScope().name()),
            Boolean.valueOf(dataValue.isDimension()),
            dataValue.getPersistTypes().stream()
                .map(persistType -> StepDataPersistType.valueOf(persistType.name()))
                .collect(Collectors.toUnmodifiableList()),
            dataValue.getDefaultValue(),
            StepDataKeyType.valueOf(dataValue.getKeyType().name()),
            dataValue.getEnabled(),
            dataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> Id.<ComponentResponse>valueOf(reference.getComponentId().getValue()))
                .collect(Collectors.toList()),
            dataValue.getCampaignComponentReferences()
                .stream()
                .map(reference -> new ComponentReferenceResponse(Id.valueOf(reference.getComponentId().getValue()),
                    reference.getSocketNames()))
                .collect(Collectors.toList()));
    }

}
