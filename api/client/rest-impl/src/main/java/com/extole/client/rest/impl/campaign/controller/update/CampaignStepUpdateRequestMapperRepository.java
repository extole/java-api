package com.extole.client.rest.impl.campaign.controller.update;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.StepType;

@Component
public class CampaignStepUpdateRequestMapperRepository {

    private final Map<StepType, CampaignStepUpdateRequestMapper<?, ?>> mappersByStepType;

    @Autowired
    public CampaignStepUpdateRequestMapperRepository(List<CampaignStepUpdateRequestMapper<?, ?>> mappers) {
        Map<StepType, CampaignStepUpdateRequestMapper<?, ?>> mappersMap = new HashMap<>();

        for (CampaignStepUpdateRequestMapper<?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getStepType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + CampaignStepUpdateRequestMapper.class.getSimpleName()
                        + " for the same step type: " + mapper.getStepType());
            }

            mappersMap.put(mapper.getStepType(), mapper);
        }

        this.mappersByStepType = Collections.unmodifiableMap(mappersMap);
    }

    public CampaignStepUpdateRequestMapper<?, ?> getUpdateRequestMapper(StepType stepType) {
        if (!mappersByStepType.containsKey(stepType)) {
            throw new RuntimeException("No instance of " + CampaignStepUpdateRequestMapper.class.getSimpleName()
                + " found for step type=" + stepType);
        }

        return mappersByStepType.get(stepType);
    }

}
