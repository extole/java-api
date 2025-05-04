package com.extole.client.rest.zone;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuiltCampaignStepMappingZoneResponse {

    private final String name;

    public BuiltCampaignStepMappingZoneResponse(
        @JsonProperty("name") String name) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

}
