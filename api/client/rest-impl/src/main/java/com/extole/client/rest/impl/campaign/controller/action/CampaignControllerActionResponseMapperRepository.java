package com.extole.client.rest.impl.campaign.controller.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class CampaignControllerActionResponseMapperRepository {

    private final Map<CampaignControllerActionType, CampaignControllerActionResponseMapper<?, ?, ?>> mappersByType;

    @Autowired
    public CampaignControllerActionResponseMapperRepository(
        List<CampaignControllerActionResponseMapper<?, ?, ?>> mappers) {
        Map<CampaignControllerActionType, CampaignControllerActionResponseMapper<?, ?, ?>> mappersMap = new HashMap<>();

        for (CampaignControllerActionResponseMapper<?, ?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getActionType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + CampaignControllerActionResponseMapper.class.getSimpleName()
                        + " for the same action type: " + mapper.getActionType());
            }

            mappersMap.put(mapper.getActionType(), mapper);
        }

        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public CampaignControllerActionResponseMapper<?, ?, ?> getMapper(CampaignControllerActionType actionType) {
        if (!mappersByType.containsKey(actionType)) {
            throw new RuntimeException("No instance of " + CampaignControllerActionResponseMapper.class.getSimpleName()
                + " found for action type=" + actionType);
        }

        return mappersByType.get(actionType);
    }

}
