package com.extole.api.impl.client;

import com.extole.api.client.Client;
import com.extole.client.identity.IdentityKey;

public final class ClientImpl implements Client {
    private final String id;
    private final String name;
    private final String shortName;
    private final String clientType;
    private final String timezone;
    private final String identityKey;

    public ClientImpl(String id,
        String name,
        String shortName,
        String clientType,
        String timezone,
        String identityKey) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.clientType = clientType;
        this.timezone = timezone;
        // TODO remove null check and replace with schema migration ENG-24675
        this.identityKey = identityKey == null ? IdentityKey.EMAIL_IDENTITY_KEY.getName() : identityKey;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getClientType() {
        return clientType;
    }

    @Override
    public String getTimezone() {
        return timezone;
    }

    @Override
    public String getIdentityKey() {
        return identityKey;
    }
}
