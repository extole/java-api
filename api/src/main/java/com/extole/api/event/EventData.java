package com.extole.api.event;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface EventData {
    enum Source {
        JWT, PREHANDLER, REQUEST_BODY, REQUEST_QUERY_PARAMETER, BACKEND, EXPIRED_JWT
    }

    String getName();

    Object getValue();

    EventData.Source getSource();

    boolean isVerified();
}
