package com.extole.consumer.rest.impl.report.cache;

import java.util.Objects;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.consumer.rest.report.ReportFormat;
import com.extole.id.Id;

public final class IdAuthorizationFormatKey {
    private final Id<?> id;
    private final Id<ClientHandle> clientId;
    private final Authorization authorization;
    private final ReportFormat format;

    public IdAuthorizationFormatKey(Id<?> id, Authorization authorization, ReportFormat format) {
        this.id = id;
        this.clientId = authorization.getClientId();
        this.authorization = authorization;
        this.format = format;
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

    public ReportFormat getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        IdAuthorizationFormatKey that = (IdAuthorizationFormatKey) other;
        return Objects.equals(id, that.id)
            && Objects.equals(clientId, that.clientId)
            && Objects.equals(format, that.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, format);
    }
}
