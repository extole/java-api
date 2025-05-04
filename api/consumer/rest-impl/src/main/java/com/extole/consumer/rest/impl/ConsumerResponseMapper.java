package com.extole.consumer.rest.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.consumer.rest.me.PublicPersonStepResponse;
import com.extole.id.Id;
import com.extole.person.service.profile.step.PersonStepData;
import com.extole.person.service.profile.step.PublicPersonStep;

@Component
public class ConsumerResponseMapper {

    public List<PublicPersonStepResponse> toPublicPersonStepResponse(List<? extends PublicPersonStep> personSteps) {
        return personSteps.stream()
            .map(this::mapToPublicStepResponse)
            .collect(Collectors.toList());
    }

    private PublicPersonStepResponse mapToPublicStepResponse(PublicPersonStep step) {
        return new PublicPersonStepResponse(step.getStepName(),
            step.getEventDate().toString(),
            step.getProgramLabel().orElse(null),
            step.getCampaignId().map(Id::getValue).orElse(null),
            step.getPublicData().stream()
                .collect(Collectors.toMap(PersonStepData::getName, PersonStepData::getValue)),
            step.getJourneyName().map(value -> value.getValue()).orElse(null));
    }

}
