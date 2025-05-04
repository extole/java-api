package com.extole.api.impl.model;

import com.extole.api.model.Client;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.client.ClientPojo;

final class ClientImpl implements Client {
    private final ClientPojo client;

    ClientImpl(ClientPojo client) {
        this.client = client;
    }

    @Override
    public String getId() {
        return client.getId().getValue();
    }

    @Override
    public String getName() {
        return client.getName();
    }

    @Override
    public String getShortName() {
        return client.getShortName();
    }

    @Override
    public String getClientType() {
        return client.getClientType().name();
    }

    @Override
    public String getTimezone() {
        return client.getTimeZone().getId();
    }

    @Override
    public String getIdentityKey() {
        return client.getIdentityKey().getName();
    }

    @Override
    public String getCreatedDate() {
        return client.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return client.getUpdatedDate().toString();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
