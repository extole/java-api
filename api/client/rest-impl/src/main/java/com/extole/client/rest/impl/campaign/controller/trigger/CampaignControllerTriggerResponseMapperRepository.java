package com.extole.client.rest.impl.campaign.controller.trigger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class CampaignControllerTriggerResponseMapperRepository {

    private final Map<CampaignControllerTriggerType, CampaignControllerTriggerResponseMapper<?, ?, ?>> mappersByType;

    @Autowired
    public CampaignControllerTriggerResponseMapperRepository(
        List<CampaignControllerTriggerResponseMapper<?, ?, ?>> mappers) {
        Map<CampaignControllerTriggerType, CampaignControllerTriggerResponseMapper<?, ?, ?>> mappersMap =
            new HashMap<>();

        for (CampaignControllerTriggerResponseMapper<?, ?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getTriggerType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + CampaignControllerTriggerResponseMapper.class.getSimpleName()
                        + " for the same trigger type: " + mapper.getTriggerType());
            }

            mappersMap.put(mapper.getTriggerType(), mapper);
        }

        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public CampaignControllerTriggerResponseMapper<?, ?, ?> getMapper(CampaignControllerTriggerType triggerType) {
        if (!mappersByType.containsKey(triggerType)) {
            throw new RuntimeException("No instance of " + CampaignControllerTriggerResponseMapper.class.getSimpleName()
                + " found for trigger type=" + triggerType);
        }

        return mappersByType.get(triggerType);
    }

}
