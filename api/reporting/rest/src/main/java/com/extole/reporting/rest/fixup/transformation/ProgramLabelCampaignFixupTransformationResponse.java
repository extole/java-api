package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProgramLabelCampaignFixupTransformationResponse extends FixupTransformationResponse {

    private static final String JSON_PROGRAM_LABEL = "program_label";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";

    private final String programLabel;
    private final String campaignId;

    @JsonCreator
    public ProgramLabelCampaignFixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type,
        @JsonProperty(JSON_PROGRAM_LABEL) String programLabel,
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId) {
        super(id, type);
        this.programLabel = programLabel;
        this.campaignId = campaignId;
    }

    @JsonProperty(JSON_PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }
}
