package com.extole.client.rest.client.core;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ClientCoreAssetsVersionResponse {
    private static final String JSON_CLIENT_ID = "client_id";
    private static final String JSON_CLIENT_VERSION = "client_version";
    private static final String JSON_CORE_ASSETS_VERSION = "core_assets_version";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_DEBUG_MESSAGE = "debug_message";

    private final String clientId;
    private final Integer clientVersion;
    private final Long coreAssetsVersion;
    private final ZonedDateTime createdDate;
    private final String debugMessage;

    @JsonCreator
    public ClientCoreAssetsVersionResponse(
        @JsonProperty(JSON_CLIENT_ID) String clientId,
        @JsonProperty(JSON_CLIENT_VERSION) Integer clientVersion,
        @JsonProperty(JSON_CORE_ASSETS_VERSION) Long coreAssetsVersion,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_DEBUG_MESSAGE) String debugMessage) {
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.coreAssetsVersion = coreAssetsVersion;
        this.createdDate = createdDate;
        this.debugMessage = debugMessage;
    }

    @JsonProperty(JSON_CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(JSON_CLIENT_VERSION)
    public Integer getClientVersion() {
        return clientVersion;
    }

    @JsonProperty(JSON_CORE_ASSETS_VERSION)
    public Long getCoreAssetsVersion() {
        return coreAssetsVersion;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @Nullable
    @JsonProperty(JSON_DEBUG_MESSAGE)
    public String getDebugMessage() {
        return debugMessage;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
