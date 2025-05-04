package com.extole.client.rest.impl.component.type;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.component.type.ComponentTypeResponse;
import com.extole.model.entity.component.type.ComponentType;

@Component
public class ComponentTypeRestMapper {

    public ComponentTypeResponse toComponentTypeResponse(ComponentType componentType, ZoneId timeZone) {
        return new ComponentTypeResponse(componentType.getName(),
            componentType.getDisplayName(),
            componentType.getSchema(),
            componentType.getParent(),
            componentType.getCreatedDate().atZone(timeZone),
            componentType.getUpdatedDate().atZone(timeZone));
    }

}
