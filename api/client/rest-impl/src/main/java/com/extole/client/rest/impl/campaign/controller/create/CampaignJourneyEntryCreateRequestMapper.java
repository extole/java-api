package com.extole.client.rest.impl.campaign.controller.create;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.create.CampaignJourneyEntryCreateRequest;
import com.extole.client.rest.impl.campaign.component.ComponentReferenceRequestMapper;
import com.extole.evaluateable.Evaluatables;
import com.extole.model.entity.campaign.CampaignJourneyEntry;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.journey.entry.CampaignJourneyEntryBuilder;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

@Component
public class CampaignJourneyEntryCreateRequestMapper
    implements CampaignStepCreateRequestMapper<CampaignJourneyEntryCreateRequest, CampaignJourneyEntry> {

    private final ComponentReferenceRequestMapper componentReferenceRequestMapper;

    @Autowired
    public CampaignJourneyEntryCreateRequestMapper(ComponentReferenceRequestMapper componentReferenceRequestMapper) {
        this.componentReferenceRequestMapper = componentReferenceRequestMapper;
    }

    @Override
    public StepType getStepType() {
        return StepType.JOURNEY_ENTRY;
    }

    @Override
    public CampaignJourneyEntry create(Authorization authorization, CampaignBuilder campaignBuilder,
        CampaignJourneyEntryCreateRequest createRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, BuildCampaignException, InvalidComponentReferenceException,
        CampaignStepBuildException {
        CampaignJourneyEntryBuilder journeyEntryBuilder = campaignBuilder.addJourneyEntry();

        new GenericCampaignStepCreateRequestMapper(journeyEntryBuilder, componentReferenceRequestMapper)
            .apply(createRequest);

        createRequest.getJourneyName().ifPresent(journeyName -> {
            journeyEntryBuilder
                .withJourneyName(Evaluatables.remapClassToClass(journeyName, new TypeReference<>() {}));
        });

        createRequest.getPriority().ifPresent(priority -> {
            journeyEntryBuilder.withPriority(priority);
        });

        createRequest.getKey().ifPresent(key -> {
            journeyEntryBuilder.withKey()
                .withName(key.getName())
                .withValue(key.getValue());
        });

        return journeyEntryBuilder.save();
    }

}
