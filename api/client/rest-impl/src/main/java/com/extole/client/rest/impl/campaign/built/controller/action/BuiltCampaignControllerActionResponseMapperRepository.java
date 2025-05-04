package com.extole.client.rest.impl.campaign.built.controller.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.model.entity.campaign.CampaignControllerActionType;

@Component
public class BuiltCampaignControllerActionResponseMapperRepository {

    private final Map<CampaignControllerActionType, BuiltCampaignControllerActionResponseMapper<?, ?>> mappersByType;

    @Autowired
    public BuiltCampaignControllerActionResponseMapperRepository(
        List<BuiltCampaignControllerActionResponseMapper<?, ?>> mappers) {
        Map<CampaignControllerActionType, BuiltCampaignControllerActionResponseMapper<?, ?>> mappersMap =
            new HashMap<>();

        for (BuiltCampaignControllerActionResponseMapper<?, ?> mapper : mappers) {
            if (mappersMap.containsKey(mapper.getActionType())) {
                throw new IllegalStateException(
                    "Found multiple instances of " + BuiltCampaignControllerActionResponseMapper.class.getSimpleName()
                        + " for the same action type: " + mapper.getActionType());
            }

            mappersMap.put(mapper.getActionType(), mapper);
        }

        this.mappersByType = Collections.unmodifiableMap(mappersMap);
    }

    public BuiltCampaignControllerActionResponseMapper<?, ?> getMapper(CampaignControllerActionType actionType) {
        if (!mappersByType.containsKey(actionType)) {
            throw new RuntimeException(
                "No instance of " + BuiltCampaignControllerActionResponseMapper.class.getSimpleName()
                    + " found for action type=" + actionType);
        }

        return mappersByType.get(actionType);
    }

}
