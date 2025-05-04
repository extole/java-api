package com.extole.api.impl.event.client;

import com.extole.api.event.client.ModelConfiguration;
import com.extole.common.lang.ToString;

public final class ModelConfigurationImpl<T> implements ModelConfiguration<T> {
    private final String clientId;
    private final Integer clientVersion;
    private final String entityId;
    private final String userId;
    private final String eventTime;
    private final String operation;
    private final String type;
    private final T entity;

    public ModelConfigurationImpl(String clientId, Integer clientVersion, String entityId, String userId,
        String eventTime, String operation, String type, T entity) {
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.entityId = entityId;
        this.userId = userId;
        this.eventTime = eventTime;
        this.operation = operation;
        this.type = type;
        this.entity = entity;
    }

    public String getClientId() {
        return clientId;
    }

    public Integer getClientVersion() {
        return clientVersion;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getOperation() {
        return operation;
    }

    public String getType() {
        return type;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
