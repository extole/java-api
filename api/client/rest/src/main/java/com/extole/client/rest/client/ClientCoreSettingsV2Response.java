package com.extole.client.rest.client;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO to be removed in ENG-14116
public class ClientCoreSettingsV2Response {

    private final String coreVersion;

    public ClientCoreSettingsV2Response(@JsonProperty("core_version") String coreVersion) {
        this.coreVersion = coreVersion;
    }

    @JsonProperty("core_version")
    public String getCoreVersion() {
        return coreVersion;
    }

}
