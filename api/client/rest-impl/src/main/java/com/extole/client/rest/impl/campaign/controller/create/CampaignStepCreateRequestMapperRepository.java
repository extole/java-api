package com.extole.client.rest.impl.campaign.controller.create;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.controller.StepType;

@Component
public class CampaignStepCreateRequestMapperRepository {

    private final Map<StepType, CampaignStepCreateRequestMapper<?, ?>> mappersByStepType;

    @Autowired
    public CampaignStepCreateRequestMapperRepository(List<CampaignStepCreateRequestMapper<?, ?>> mappers) {
        Map<StepType, CampaignStepCreateRequestMapper<?, ?>> mappersMap = new HashMap<>();

        for (CampaignStepCreateRequestMapper<?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getStepType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + CampaignStepCreateRequestMapper.class.getSimpleName()
                        + " for the same step type: " + mapper.getStepType());
            }

            mappersMap.put(mapper.getStepType(), mapper);
        }

        this.mappersByStepType = Collections.unmodifiableMap(mappersMap);
    }

    public CampaignStepCreateRequestMapper<?, ?> getCreateRequestMapper(StepType stepType) {
        if (!mappersByStepType.containsKey(stepType)) {
            throw new RuntimeException("No instance of " + CampaignStepCreateRequestMapper.class.getSimpleName()
                + " found for step type=" + stepType);
        }

        return mappersByStepType.get(stepType);
    }

}
