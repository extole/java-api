package com.extole.client.rest.campaign.built.flow.step;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class BuiltCampaignFlowStepAppTypeResponse {

    private static final String JSON_NAME = "name";

    private final String name;

    @JsonCreator
    public BuiltCampaignFlowStepAppTypeResponse(@JsonProperty(JSON_NAME) String name) {
        this.name = name;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
