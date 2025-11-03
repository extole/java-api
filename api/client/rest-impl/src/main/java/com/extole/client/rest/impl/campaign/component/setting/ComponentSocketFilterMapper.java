package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.ComponentFacetSocketFilterResponse;
import com.extole.client.rest.campaign.component.setting.ComponentTypeSocketFilterResponse;
import com.extole.client.rest.campaign.component.setting.SocketFilterResponse;
import com.extole.client.rest.campaign.configuration.ComponentFacetSocketFilterConfiguration;
import com.extole.client.rest.campaign.configuration.ComponentTypeSocketFilterConfiguration;
import com.extole.client.rest.campaign.configuration.SocketFilterConfiguration;
import com.extole.model.entity.campaign.ComponentFacetSocketFilter;
import com.extole.model.entity.campaign.ComponentTypeSocketFilter;
import com.extole.model.entity.campaign.SocketFilter;

@Component
public class ComponentSocketFilterMapper {

    public SocketFilterConfiguration mapToConfiguration(SocketFilter socketFilter) {
        if (socketFilter instanceof ComponentFacetSocketFilter) {
            return mapToConfiguration((ComponentFacetSocketFilter) socketFilter);
        }
        if (socketFilter instanceof ComponentTypeSocketFilter) {
            return mapToConfiguration((ComponentTypeSocketFilter) socketFilter);
        }
        throw new IllegalArgumentException("Can't map the type " + socketFilter.getClass() + " to response");
    }

    public SocketFilterResponse mapToSocketFilter(SocketFilter socketFilter) {
        if (socketFilter instanceof ComponentFacetSocketFilter) {
            return map((ComponentFacetSocketFilter) socketFilter);
        }
        if (socketFilter instanceof ComponentTypeSocketFilter) {
            return map((ComponentTypeSocketFilter) socketFilter);
        }
        throw new IllegalArgumentException("Can't map the type " + socketFilter.getClass() + " to response");
    }

    private ComponentFacetSocketFilterResponse map(ComponentFacetSocketFilter facetSocketFilter) {
        return new ComponentFacetSocketFilterResponse(facetSocketFilter.getName(), facetSocketFilter.getValue());
    }

    private ComponentTypeSocketFilterResponse map(ComponentTypeSocketFilter componentTypeSocketFilter) {
        return new ComponentTypeSocketFilterResponse(componentTypeSocketFilter.getComponentType());
    }

    private ComponentFacetSocketFilterConfiguration mapToConfiguration(ComponentFacetSocketFilter facetSocketFilter) {
        return new ComponentFacetSocketFilterConfiguration(facetSocketFilter.getName(), facetSocketFilter.getValue());
    }

    private ComponentTypeSocketFilterConfiguration
        mapToConfiguration(ComponentTypeSocketFilter componentTypeSocketFilter) {
        return new ComponentTypeSocketFilterConfiguration(componentTypeSocketFilter.getComponentType());
    }

}
