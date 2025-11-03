package com.extole.client.rest.impl.component.facet;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.client.rest.component.facet.ComponentFacetAllowedValueResponse;
import com.extole.client.rest.component.facet.ComponentFacetResponse;
import com.extole.model.entity.component.facet.ComponentFacet;

@Component
public class ComponentFacetRestMapper {

    public ComponentFacetResponse toComponentFacetResponse(ComponentFacet componentFacet, ZoneId timeZone) {
        return new ComponentFacetResponse(componentFacet.getName(),
            componentFacet.getDisplayName(),
            componentFacet.getAllowedValues().stream()
                .map(value -> new ComponentFacetAllowedValueResponse(value.getValue(), value.getIcon(),
                    value.getDescription(), value.getDisplayName(), value.getColor()))
                .toList(),
            componentFacet.getCreatedDate().atZone(timeZone),
            componentFacet.getUpdatedDate().atZone(timeZone));
    }

}
