package com.extole.client.rest.campaign.component.setting.variable.batch;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableUpdateRequest;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class BatchVariableUpdateRequest {

    private static final String JSON_COMPONENT_VARIABLE_VALUES = "variables";

    private final Omissible<Map<String, CampaignComponentVariableUpdateRequest>> variables;

    @JsonCreator
    private BatchVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<
            Map<String, CampaignComponentVariableUpdateRequest>> variables) {
        this.variables = variables;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES)
    public Omissible<Map<String, CampaignComponentVariableUpdateRequest>> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Map<String, CampaignComponentVariableUpdateRequest>> variables = Omissible.omitted();

        private Builder() {

        }

        public Builder withVariables(Map<String, CampaignComponentVariableUpdateRequest> variables) {
            this.variables = Omissible.of(variables);
            return this;
        }

        public BatchVariableUpdateRequest build() {
            return new BatchVariableUpdateRequest(variables);
        }
    }

}
