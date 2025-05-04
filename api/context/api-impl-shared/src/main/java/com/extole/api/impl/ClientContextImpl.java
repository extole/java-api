package com.extole.api.impl;

import com.extole.api.ClientContext;
import com.extole.common.lang.ToString;

public class ClientContextImpl implements ClientContext {
    private final String clientId;
    private final String clientShortName;
    private final String timezone;

    public ClientContextImpl(String clientId, String clientShortName, String timezone) {
        this.clientId = clientId;
        this.clientShortName = clientShortName;
        this.timezone = timezone;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientShortName() {
        return clientShortName;
    }

    @Override
    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
