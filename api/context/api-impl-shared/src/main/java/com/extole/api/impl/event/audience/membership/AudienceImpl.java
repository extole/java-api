package com.extole.api.impl.event.audience.membership;

import com.extole.api.event.audience.membership.Audience;
import com.extole.common.lang.ToString;

public class AudienceImpl implements Audience {
    private final String clientId;

    private final String id;
    private final String name;

    public AudienceImpl(String clientId, String id, String name) {
        this.clientId = clientId;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getClientId() {
        return clientId;
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
    public String toString() {
        return ToString.create(this);
    }

}
