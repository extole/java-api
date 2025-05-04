package com.extole.client.rest.impl.campaign.controller.update;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.update.CampaignFrontendControllerUpdateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Component
class CampaignFrontendControllerUpdateRequestMapper
    implements CampaignStepUpdateRequestMapper<CampaignFrontendControllerUpdateRequest, FrontendController> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    CampaignFrontendControllerUpdateRequestMapper(ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.FRONTEND_CONTROLLER;
    }

    @Override
    public FrontendController update(Authorization authorization, CampaignBuilder campaignBuilder,
        FrontendController controller, CampaignFrontendControllerUpdateRequest updateRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, BuildCampaignException, InvalidComponentReferenceException,
        CampaignStepBuildException {
        FrontendControllerBuilder controllerBuilder = campaignBuilder.updateFrontendController(controller);

        updateRequest.getName().ifPresent(value -> {
            controllerBuilder.withName(value);
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

        updateRequest.getCategory().ifPresent(category -> {
            controllerBuilder.withCategory(category);
        });

        updateRequest.getAliases().ifPresent(aliases -> {
            controllerBuilder.withAliases(aliases);
        });

        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(controllerBuilder, componentIds);
        });

        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(controllerBuilder, componentReferences);
        });

        updateRequest.getSendPolicy().ifPresent(
            sendPolicy -> controllerBuilder
                .withSendPolicy(Evaluatables.remapEnum(sendPolicy, new TypeReference<>() {})));

        return controllerBuilder.save();
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
