package com.extole.client.rest.impl.campaign.controller.update;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.update.CampaignJourneyEntryUpdateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.evaluateable.Evaluatables;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignJourneyEntry;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ComponentElementBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryBuilder;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryKeyBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Component
public class CampaignJourneyEntryUpdateRequestMapper
    implements CampaignStepUpdateRequestMapper<CampaignJourneyEntryUpdateRequest, CampaignJourneyEntry> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignJourneyEntryUpdateRequestMapper(ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.JOURNEY_ENTRY;
    }

    @Override
    public CampaignJourneyEntry update(Authorization authorization, CampaignBuilder campaignBuilder,
        CampaignJourneyEntry journeyEntry, CampaignJourneyEntryUpdateRequest updateRequest)
        throws ConcurrentCampaignUpdateException, InvalidComponentReferenceException, BuildCampaignException,
        StaleCampaignVersionException, CampaignStepBuildException, CampaignComponentValidationRestException {
        CampaignJourneyEntryBuilder journeyEntryBuilder = campaignBuilder.updateJourneyEntry(journeyEntry);

        updateRequest.getEnabled().ifPresent(enabled -> {
            journeyEntryBuilder.withEnabled(enabled);
        });

        updateRequest.getComponentIds().ifPresent(componentIds -> {
            handleComponentIds(journeyEntryBuilder, componentIds);
        });

        updateRequest.getComponentReferences().ifPresent(componentReferences -> {
            componentReferenceRequestMapper.handleComponentReferences(journeyEntryBuilder, componentReferences);
        });

        updateRequest.getPriority().ifPresent(priority -> {
            journeyEntryBuilder.withPriority(priority);
        });

        updateRequest.getJourneyName().ifPresent(journeyName -> {
            journeyEntryBuilder.withJourneyName(Evaluatables.remapClassToClass(journeyName, new TypeReference<>() {}));
        });

        updateRequest.getKey().ifPresent(key -> {
            if (key.isPresent()) {
                CampaignJourneyEntryKeyBuilder keyBuilder = journeyEntryBuilder.withKey();
                key.get().getName().ifPresent(keyName -> keyBuilder.withName(keyName));
                key.get().getValue().ifPresent(keyValue -> keyBuilder.withValue(keyValue));
            } else {
                journeyEntryBuilder.removeKey();
            }
        });

        return journeyEntryBuilder.save();
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
