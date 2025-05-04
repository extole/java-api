package com.extole.client.rest.impl.campaign.controller.update;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.update.CampaignControllerUpdateRequest;
import com.extole.client.rest.campaign.step.data.StepDataCreateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.StepData;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuilder;

@Component
public class CampaignControllerUpdateRequestMapper
    implements CampaignStepUpdateRequestMapper<CampaignControllerUpdateRequest, CampaignController> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerUpdateRequestMapper(ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.CONTROLLER;
    }

    @Override
    public CampaignController update(Authorization authorization, CampaignBuilder campaignBuilder,
        CampaignController controller, CampaignControllerUpdateRequest updateRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, BuildCampaignException, InvalidComponentReferenceException,
        CampaignStepBuildException {
        CampaignControllerBuilder controllerBuilder = campaignBuilder.updateController(controller);

        updateRequest.getName().ifPresent(name -> {
            controllerBuilder.withName(name);
        });

        updateRequest.getScope().ifPresent(scope -> {
            controllerBuilder.withScope(Evaluatables.remapEnum(scope, new TypeReference<>() {}));
        });

        updateRequest.getEnabled().ifPresent(enabled -> {
            controllerBuilder.withEnabled(enabled);
        });

        updateRequest.getEnabledOnStates().ifPresent(enabledOnStates -> {
            controllerBuilder
                .withEnabledOnStates(Evaluatables.remapEnumCollection(enabledOnStates, new TypeReference<>() {}));
        });

        updateRequest.getJourneyNames().ifPresent(journeyNames -> {
            controllerBuilder.withJourneyNames(Evaluatables.remapCollection(journeyNames, new TypeReference<>() {}));
        });

        updateRequest.getSelectors().ifPresent(selectors -> {
            controllerBuilder.withSelectors(Evaluatables.remapEnumCollection(selectors, new TypeReference<>() {}));
        });

        updateRequest.getAliases().ifPresent(aliases -> {
            controllerBuilder.withAliases(aliases);
        });

        updateRequest.getData().ifPresent(data -> {
            if (!data.isEmpty()) {
                controllerBuilder.clearData();
                addData(controllerBuilder, data, updateRequest.getComponentIds(),
                    updateRequest.getComponentReferences());
            } else {
                controllerBuilder.clearData();
            }
        });

        updateRequest.getSendPolicy().ifPresent(sendPolicy -> controllerBuilder
            .withSendPolicy(Evaluatables.remapEnum(sendPolicy, new TypeReference<>() {})));

        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(controllerBuilder, componentIds);
            if (updateRequest.getData().isOmitted()) {
                for (StepData stepData : controller.getData()) {
                    StepDataBuilder dataBuilder = controllerBuilder.updateStepData(stepData);
                    handleComponentIds(dataBuilder, componentIds);
                }
            }
        });

        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(controllerBuilder, componentReferences);
            if (updateRequest.getData().isOmitted()) {
                for (StepData stepData : controller.getData()) {
                    StepDataBuilder dataBuilder = controllerBuilder.updateStepData(stepData);
                    componentReferenceRequestMapper.handleComponentReferences(dataBuilder, componentReferences);
                }
            }
        });

        return controllerBuilder.save();
    }

    private void addData(CampaignControllerBuilder controllerBuilder, List<StepDataCreateRequest> data,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences)
        throws CampaignComponentValidationRestException {
        for (StepDataCreateRequest dataValue : data) {
            StepDataBuilder stepDataBuilder = controllerBuilder.addData();

            dataValue.getName().ifDefined((value) -> stepDataBuilder.withName(value));
            dataValue.getValue().ifDefined((value) -> stepDataBuilder.withValue(value));
            dataValue.getScope()
                .ifPresent(scope -> stepDataBuilder.withScope(Evaluatables.remapEnum(scope, new TypeReference<>() {})));
            dataValue.isDimension().ifPresent(dimension -> stepDataBuilder.withDimension(dimension));
            dataValue.getPersistTypes()
                .ifPresent(persistTypes -> stepDataBuilder
                    .withPersistTypes(Evaluatables.remapEnumCollection(persistTypes, new TypeReference<>() {})));
            dataValue.getDefaultValue().ifPresent(defaultValue -> stepDataBuilder.withDefaultValue(defaultValue));
            dataValue.getKeyType().ifPresent(
                keyType -> stepDataBuilder.withKeyType(Evaluatables.remapEnum(keyType, new TypeReference<>() {})));
            dataValue.getEnabled().ifPresent(enabled -> stepDataBuilder.withEnabled(enabled));
            componentIds.ifPresent(candidates -> {
                handleComponentIds(stepDataBuilder, candidates);
            });
            componentReferences.ifPresent(candidates -> {
                componentReferenceRequestMapper.handleComponentReferences(stepDataBuilder, candidates);
            });
        }
    }

    private void handleComponentIds(ComponentElementBuilder elementBuilder,
        List<Id<ComponentResponse>> componentIds) throws CampaignComponentValidationRestException {
        elementBuilder.clearComponentReferences();
        for (Id<ComponentResponse> componentId : componentIds) {
            if (componentId == null) {
                throw RestExceptionBuilder.newBuilder(CampaignComponentValidationRestException.class)
                    .withErrorCode(CampaignComponentValidationRestException.REFERENCE_COMPONENT_ID_MISSING)
                    .build();
            }
            elementBuilder.addComponentReference(Id.valueOf(componentId.getValue()));
        }
    }
}
