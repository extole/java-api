package com.extole.client.rest.campaign.program;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CampaignProgramUpdateRequest {
    private static final String PROGRAM_TYPE = "program_type";

    private final String programType;

    public CampaignProgramUpdateRequest(@JsonProperty(PROGRAM_TYPE) String programType) {
        this.programType = programType;
    }

    @JsonProperty(PROGRAM_TYPE)
    public String getProgramType() {
        return programType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String programType;

        private Builder() {

        }

        public Builder withProgramType(String programType) {
            this.programType = programType;
            return this;
        }

        public CampaignProgramUpdateRequest build() {
            return new CampaignProgramUpdateRequest(programType);
        }
    }
}
