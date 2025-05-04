package com.extole.client.rest.impl.campaign.controller.create;

import java.util.List;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.create.CampaignStepCreateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuilder;

public final class GenericCampaignStepCreateRequestMapper {

    private final CampaignStepBuilder<?, ?> stepBuilder;
    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    public GenericCampaignStepCreateRequestMapper(CampaignStepBuilder<?, ?> stepBuilder,
        ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.stepBuilder = stepBuilder;
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    public void apply(CampaignStepCreateRequest createRequest) throws CampaignComponentValidationRestException {
        createRequest.getEnabled().ifPresent(enabled -> {
            stepBuilder.withEnabled(enabled);
        });

        createRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(stepBuilder, componentIds);
        });
        createRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(stepBuilder, componentReferences);
        });
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
