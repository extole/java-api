package com.extole.client.rest.impl.prehandler.action.response;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.action.DataAttributeMapping;
import com.extole.client.rest.prehandler.action.response.MapDataAttributesPrehandlerActionResponse;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.action.MapDataAttributesPrehandlerAction;

@Component
public class MapDataAttributesPrehandleActionResponseMapper implements
    PrehandlerActionResponseMapper<MapDataAttributesPrehandlerAction, MapDataAttributesPrehandlerActionResponse> {

    @Override
    public MapDataAttributesPrehandlerActionResponse toResponse(MapDataAttributesPrehandlerAction action) {
        return new MapDataAttributesPrehandlerActionResponse(action.getId().getValue(),
            action.getMappings().stream()
                .map(mapping -> new DataAttributeMapping(mapping.getAttribute(), mapping.getSourceAttribute(),
                    mapping.getDefaultValue().orElse(null)))
                .collect(Collectors.toList()));
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.MAP_DATA_ATTRIBUTES;
    }
}
