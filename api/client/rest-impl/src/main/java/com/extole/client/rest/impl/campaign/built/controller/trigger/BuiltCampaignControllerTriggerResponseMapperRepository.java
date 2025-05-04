package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.CampaignControllerTriggerType;

@Component
public class BuiltCampaignControllerTriggerResponseMapperRepository {

    private final Map<CampaignControllerTriggerType, BuiltCampaignControllerTriggerResponseMapper<?, ?>> mappersByType;

    @Autowired
    public BuiltCampaignControllerTriggerResponseMapperRepository(
        List<BuiltCampaignControllerTriggerResponseMapper<?, ?>> mappers) {
        Map<CampaignControllerTriggerType, BuiltCampaignControllerTriggerResponseMapper<?, ?>> mappersMap =
            new HashMap<>();

        for (BuiltCampaignControllerTriggerResponseMapper<?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getTriggerType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + BuiltCampaignControllerTriggerResponseMapper.class.getSimpleName()
                        + " for the same trigger type: " + mapper.getTriggerType());
            }

            mappersMap.put(mapper.getTriggerType(), mapper);
        }

        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public BuiltCampaignControllerTriggerResponseMapper<?, ?> getMapper(CampaignControllerTriggerType triggerType) {
        if (!mappersByType.containsKey(triggerType)) {
            throw new RuntimeException(
                "No instance of " + BuiltCampaignControllerTriggerResponseMapper.class.getSimpleName()
                    + " found for trigger type=" + triggerType);
        }

        return mappersByType.get(triggerType);
    }

}
