package com.extole.client.rest.impl.campaign.controller.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.StepType;

@Component
public class CampaignStepResponseMapperRepository {

    private final Map<StepType, CampaignStepResponseMapper<?, ?, ?>> mappersByStepType;

    @Autowired
    public CampaignStepResponseMapperRepository(List<CampaignStepResponseMapper<?, ?, ?>> mappers) {
        Map<StepType, CampaignStepResponseMapper<?, ?, ?>> mappersMap = new HashMap<>();

        for (CampaignStepResponseMapper<?, ?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getStepType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + CampaignStepResponseMapper.class.getSimpleName()
                        + " for the same step type: " + mapper.getStepType());
            }

            mappersMap.put(mapper.getStepType(), mapper);
        }

        this.mappersByStepType = Collections.unmodifiableMap(mappersMap);
    }

    public CampaignStepResponseMapper<?, ?, ?> getMapper(StepType stepType) {
        if (!mappersByStepType.containsKey(stepType)) {
            throw new RuntimeException("No instance of " + CampaignStepResponseMapper.class.getSimpleName()
                + " found for step type=" + stepType);
        }

        return mappersByStepType.get(stepType);
    }

}
