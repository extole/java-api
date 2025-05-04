package com.extole.client.rest.impl.campaign.built.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.StepType;

@Component
public class BuiltCampaignStepResponseMapperRepository {

    private final Map<StepType, BuiltCampaignStepResponseMapper<?, ?>> mappersByStepType;

    @Autowired
    public BuiltCampaignStepResponseMapperRepository(List<BuiltCampaignStepResponseMapper<?, ?>> mappers) {
        Map<StepType, BuiltCampaignStepResponseMapper<?, ?>> mappersMap = new HashMap<>();

        for (BuiltCampaignStepResponseMapper<?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getStepType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + BuiltCampaignStepResponseMapper.class.getSimpleName()
                        + " for the same step type: " + mapper.getStepType());
            }

            mappersMap.put(mapper.getStepType(), mapper);
        }

        this.mappersByStepType = Collections.unmodifiableMap(mappersMap);
    }

    public BuiltCampaignStepResponseMapper<?, ?> getMapper(StepType stepType) {
        if (!mappersByStepType.containsKey(stepType)) {
            throw new RuntimeException("No instance of " + BuiltCampaignStepResponseMapper.class.getSimpleName()
                + " found for step type=" + stepType);
        }

        return mappersByStepType.get(stepType);
    }

}
