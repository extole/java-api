package com.extole.api.event.client;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ClientEvent {

    String getEventId();

    String getEventType();

    String getClientId();

    String getEventTime();

    String getName();

    String[] getTags();

    String getMessage();

    Map<String, DataValue> getData();

    String getLevel();

    @Nullable
    String getUserId();

    String getScope();

    interface DataValue {

        String getValue();

        String getType();

        String getScope();
    }
}
