package com.extole.security.backend;

import java.time.Instant;
import java.util.Set;

import com.google.common.base.Preconditions;

import com.extole.authorization.service.ClientHandle;
import com.extole.authorization.service.Identity;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

final class BackendAuthorizationImpl implements BackendAuthorization {

    private static final String ID_PATTERN = "%s-%s";

    private final Id<BackendAuthorization> id;
    private final Id<ClientHandle> clientId;
    private final Instant createdAt;
    private final Set<Scope> scopes;

    BackendAuthorizationImpl(String instanceName,
        Id<ClientHandle> clientId,
        Set<Scope> scopes) {
        this.id = Id.valueOf(String.format(ID_PATTERN, instanceName, clientId));
        this.clientId = clientId;
        this.createdAt = Instant.now();
        this.scopes = Set.copyOf(scopes);
    }

    @Override
    public Id<?> getId() {
        return id;
    }

    @Override
    public Id<?> getIdentityId() {
        return id;
    }

    @Override
    public Id<ClientHandle> getClientId() {
        return clientId;
    }

    @Override
    public Type getType() {
        return Type.BACKEND;
    }

    @Override
    public String getAccessToken() {
        return id.getValue();
    }

    @Override
    public Identity getIdentity() {
        return this;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public Instant getUpdatedAt() {
        return createdAt;
    }

    @Override
    public Instant getExpiresAt() {
        return Instant.MAX;
    }

    @Override
    public boolean isRefreshable() {
        return false;
    }

    @Override
    public Set<Scope> getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder create() {
        return new Builder();
    }

    static final class Builder {
        private String instanceName;
        private Id<ClientHandle> clientId;
        private Set<Scope> scopes;

        private Builder() {
        }

        public Builder withInstanceName(String instanceName) {
            this.instanceName = instanceName;
            return this;
        }

        public Builder withClientId(Id<ClientHandle> clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withScopes(Set<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public BackendAuthorization build() {
            validate();
            return new BackendAuthorizationImpl(instanceName, clientId, scopes);
        }

        private void validate() {
            Preconditions.checkNotNull(instanceName);
            Preconditions.checkNotNull(clientId);
            Preconditions.checkNotNull(scopes);
        }
    }

}
