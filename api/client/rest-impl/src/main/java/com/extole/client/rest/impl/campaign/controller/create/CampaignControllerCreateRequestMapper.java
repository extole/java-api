package com.extole.client.rest.impl.campaign.controller.create;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.create.CampaignControllerCreateRequest;
import com.extole.client.rest.campaign.step.data.StepDataCreateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.controller.CampaignControllerBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;
import com.extole.model.service.campaign.step.data.StepDataBuilder;

@Component
public class CampaignControllerCreateRequestMapper
    implements CampaignStepCreateRequestMapper<CampaignControllerCreateRequest, CampaignController> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignControllerCreateRequestMapper(ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.CONTROLLER;
    }

    @Override
    public CampaignController create(Authorization authorization, CampaignBuilder campaignBuilder,
        CampaignControllerCreateRequest createRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, BuildCampaignException, InvalidComponentReferenceException,
        CampaignStepBuildException {
        CampaignControllerBuilder controllerBuilder = campaignBuilder.addController();

        new GenericCampaignStepCreateRequestMapper(controllerBuilder, componentReferenceRequestMapper)
            .apply(createRequest);

        controllerBuilder.withName(createRequest.getName());

        createRequest.getScope().ifPresent(scope -> {
            controllerBuilder.withScope(Evaluatables.remapEnum(scope, new TypeReference<>() {}));
        });

        createRequest.getEnabledOnStates().ifPresent(enabledOnStates -> {
            controllerBuilder
                .withEnabledOnStates(Evaluatables.remapEnumCollection(enabledOnStates, new TypeReference<>() {}));
        });

        createRequest.getJourneyNames().ifPresent(journeyNames -> {
            controllerBuilder.withJourneyNames(Evaluatables.remapCollection(journeyNames, new TypeReference<>() {}));
        });

        createRequest.getSelectors().ifPresent(selectors -> {
            controllerBuilder.withSelectors(Evaluatables.remapEnumCollection(selectors, new TypeReference<>() {}));
        });

        createRequest.getAliases().ifPresent(aliases -> {
            controllerBuilder.withAliases(aliases);
        });

        createRequest.getData().ifPresent(data -> {
            if (!data.isEmpty()) {
                addData(controllerBuilder, data, createRequest.getComponentIds(),
                    createRequest.getComponentReferences());
            }
        });

        createRequest.getSendPolicy().ifPresent(sendPolicy -> controllerBuilder
            .withSendPolicy(Evaluatables.remapEnum(sendPolicy, new TypeReference<>() {})));

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
            componentReferences.ifPresent(references -> {
                componentReferenceRequestMapper.handleComponentReferences(stepDataBuilder, references);
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
