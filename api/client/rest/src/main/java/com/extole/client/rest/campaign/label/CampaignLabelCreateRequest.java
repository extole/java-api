package com.extole.client.rest.campaign.label;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampaignLabelCreateRequest {
    private static final String JSON_LABEL_NAME = "name";
    private static final String JSON_LABEL_TYPE = "type";

    private final String name;
    private final CampaignLabelType type;

    public CampaignLabelCreateRequest(
        @JsonProperty(JSON_LABEL_NAME) String name,
        @JsonProperty(JSON_LABEL_TYPE) CampaignLabelType type) {
        this.name = name;
        this.type = type != null ? type : CampaignLabelType.PROGRAM;
    }

    @JsonProperty(JSON_LABEL_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_LABEL_TYPE)
    public CampaignLabelType getType() {
        return type;
    }

}
