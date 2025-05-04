package com.extole.api.impl.component;

import com.extole.api.component.ComponentReference;

public class ComponentReferenceImpl implements ComponentReference {
    private final String componentId;

    public ComponentReferenceImpl(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

}
