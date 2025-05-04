package com.extole.client.rest.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.identity.IdentityKey;
import com.extole.common.lang.ToString;

public class ClientResponse {
    @Deprecated // TODO to be removed in ENG-14116
    private static final String CORE_SETTINGS = "core_settings";

    private static final String CLIENT_ID = "client_id";
    private static final String NAME = "name";
    private static final String SHORT_NAME = "short_name";
    private static final String CLIENT_TYPE = "client_type";
    private static final String VERSION = "version";
    private static final String POD = "pod";
    private static final String IDENTITY_KEY = "identity_key";

    private final String clientId;
    private final String name;
    private final String shortName;
    private final ClientType clientType;
    private final Integer version;
    private final String pod;
    private final ClientCoreSettingsV2Response coreSettingsResponse;
    private final IdentityKey identityKey;

    public ClientResponse(
        @JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(NAME) String name,
        @JsonProperty(SHORT_NAME) String shortName,
        @JsonProperty(CLIENT_TYPE) ClientType clientType,
        @JsonProperty(VERSION) Integer version,
        @JsonProperty(POD) String pod,
        @Deprecated // TODO to be removed in ENG-14116
        @JsonProperty(CORE_SETTINGS) ClientCoreSettingsV2Response coreSettingsResponse,
        @JsonProperty(IDENTITY_KEY) IdentityKey identityKey) {
        this.clientId = clientId;
        this.name = name;
        this.shortName = shortName;
        this.clientType = clientType;
        this.version = version;
        this.pod = pod;
        this.coreSettingsResponse = coreSettingsResponse;
        this.identityKey = identityKey;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(SHORT_NAME)
    public String getShortName() {
        return shortName;
    }

    @JsonProperty(CLIENT_TYPE)
    public ClientType getClientType() {
        return clientType;
    }

    @JsonProperty(VERSION)
    public Integer getVersion() {
        return version;
    }

    @JsonProperty(POD)
    public String getPod() {
        return this.pod;
    }

    @Deprecated // TODO to be removed in ENG-14116
    @JsonProperty(CORE_SETTINGS)
    public ClientCoreSettingsV2Response getCoreSettings() {
        return coreSettingsResponse;
    }

    @JsonProperty(IDENTITY_KEY)
    public IdentityKey getIdentityKey() {
        return identityKey;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
