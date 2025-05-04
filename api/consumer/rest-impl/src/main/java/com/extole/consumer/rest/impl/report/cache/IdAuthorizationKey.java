package com.extole.consumer.rest.impl.report.cache;

import java.util.Objects;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public final class IdAuthorizationKey {
    private final Id<?> id;
    private final Id<ClientHandle> clientId;
    private final Authorization authorization;

    public IdAuthorizationKey(Id<?> id, Authorization authorization) {
        this.id = id;
        this.clientId = authorization.getClientId();
        this.authorization = authorization;
    }

    public Id<?> getId() {
        return id;
    }

    public Id<ClientHandle> getClientId() {
        return clientId;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        IdAuthorizationKey that = (IdAuthorizationKey) other;
        return Objects.equals(id, that.id)
            && Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId);
    }
}
