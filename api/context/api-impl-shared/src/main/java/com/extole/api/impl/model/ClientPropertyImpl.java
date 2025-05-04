package com.extole.api.impl.model;

import com.extole.api.model.ClientProperty;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.property.ClientPropertyPojo;

final class ClientPropertyImpl implements ClientProperty {
    private final ClientPropertyPojo clientProperty;

    ClientPropertyImpl(ClientPropertyPojo clientProperty) {
        this.clientProperty = clientProperty;
    }

    @Override
    public String getId() {
        return clientProperty.getId().getValue();
    }

    @Override
    public String getName() {
        return clientProperty.getName();
    }

    @Override
    public String getValue() {
        return clientProperty.getValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
