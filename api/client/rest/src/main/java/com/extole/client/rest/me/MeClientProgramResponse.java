package com.extole.client.rest.me;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class MeClientProgramResponse {

    private static final String CLIENT_ID = "client_id";
    private static final String PROGRAM_DOMAIN = "program_domain";

    private final String clientId;
    private final String programDomain;

    public MeClientProgramResponse(@JsonProperty(CLIENT_ID) String clientId,
        @JsonProperty(PROGRAM_DOMAIN) String programDomain) {
        this.clientId = clientId;
        this.programDomain = programDomain;
    }

    @JsonProperty(CLIENT_ID)
    public String getClientId() {
        return clientId;
    }

    @JsonProperty(PROGRAM_DOMAIN)
    public String getProgramDomain() {
        return programDomain;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
