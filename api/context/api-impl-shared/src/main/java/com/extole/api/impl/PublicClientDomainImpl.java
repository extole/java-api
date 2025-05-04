package com.extole.api.impl;

import com.extole.api.PublicClientDomain;
import com.extole.common.lang.ToString;

public class PublicClientDomainImpl implements PublicClientDomain {
    private final String clientDomain;
    private final String clientDomainId;

    public PublicClientDomainImpl(String clientDomain, String clientDomainId) {
        this.clientDomain = clientDomain;
        this.clientDomainId = clientDomainId;
    }

    @Override
    public String getClientDomain() {
        return clientDomain;
    }

    @Override
    public String getClientDomainId() {
        return clientDomainId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
