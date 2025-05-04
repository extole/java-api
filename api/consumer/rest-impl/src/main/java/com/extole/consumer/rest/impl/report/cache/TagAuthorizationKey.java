package com.extole.consumer.rest.impl.report.cache;

import java.util.Objects;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public final class TagAuthorizationKey {
    private final String tag;
    private final Id<ClientHandle> clientId;
    private final Authorization authorization;

    public TagAuthorizationKey(String tag, Authorization authorization) {
        this.tag = tag;
        this.clientId = authorization.getClientId();
        this.authorization = authorization;
    }

    public String getTag() {
        return tag;
    }

    public Id<ClientHandle> getClientId() {
        return clientId;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TagAuthorizationKey that = (TagAuthorizationKey) object;
        return Objects.equals(tag, that.tag) &&
            Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, clientId);
    }
}
