package com.extole.api.event.client;

import javax.annotation.Nullable;

public interface ModelConfiguration<T> {

    String getClientId();

    Integer getClientVersion();

    String getEntityId();

    @Nullable
    String getEventTime();

    String getOperation();

    String getType();

    T getEntity();
}
