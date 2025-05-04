package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class ProgramLabelCampaignFixupTransformationUpdateRequest {
    private static final String JSON_PROGRAM_LABEL = "program_label";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";

    private final Omissible<String> programLabel;
    private final Omissible<String> campaignId;

    @JsonCreator
    public ProgramLabelCampaignFixupTransformationUpdateRequest(
        @JsonProperty(JSON_PROGRAM_LABEL) Omissible<String> programLabel,
        @JsonProperty(JSON_CAMPAIGN_ID) Omissible<String> campaignId) {
        this.programLabel = programLabel;
        this.campaignId = campaignId;
    }

    @JsonProperty(JSON_PROGRAM_LABEL)
    public Omissible<String> getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public Omissible<String> getCampaignId() {
        return campaignId;
    }
}
