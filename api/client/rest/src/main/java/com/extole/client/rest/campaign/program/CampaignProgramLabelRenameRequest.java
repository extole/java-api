package com.extole.client.rest.campaign.program;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampaignProgramLabelRenameRequest {
    private static final String PROGRAM_LABEL = "program_label";

    private final String programLabel;

    public CampaignProgramLabelRenameRequest(
        @JsonProperty(PROGRAM_LABEL) String programLabel) {
        this.programLabel = programLabel;
    }

    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

}
