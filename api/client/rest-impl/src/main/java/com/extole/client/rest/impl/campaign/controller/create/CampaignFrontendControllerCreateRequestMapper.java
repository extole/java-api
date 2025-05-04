package com.extole.client.rest.impl.campaign.controller.create;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.create.CampaignFrontendControllerCreateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.FrontendController;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.controller.FrontendControllerBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Component
public class CampaignFrontendControllerCreateRequestMapper
    implements CampaignStepCreateRequestMapper<CampaignFrontendControllerCreateRequest, FrontendController> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignFrontendControllerCreateRequestMapper(
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.FRONTEND_CONTROLLER;
    }

    @Override
    public FrontendController create(Authorization authorization, CampaignBuilder campaignBuilder,
        CampaignFrontendControllerCreateRequest createRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, InvalidComponentReferenceException, CampaignStepBuildException,
        BuildCampaignException {
        FrontendControllerBuilder controllerBuilder = campaignBuilder.addFrontendController();

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

        createRequest.getCategory().ifPresent(controllerBuilder::withCategory);

        createRequest.getAliases().ifPresent(aliases -> {
            controllerBuilder.withAliases(aliases);
        });

        createRequest.getSendPolicy().ifPresent(sendPolicy -> {
            controllerBuilder.withSendPolicy(Evaluatables.remapEnum(sendPolicy, new TypeReference<>() {}));
        });

        return controllerBuilder.save();
    }
}
