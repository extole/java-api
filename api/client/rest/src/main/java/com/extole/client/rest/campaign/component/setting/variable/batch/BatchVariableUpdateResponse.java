package com.extole.client.rest.campaign.component.setting.variable.batch;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentVariableResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableResponse;
import com.extole.common.lang.ToString;

public final class BatchVariableUpdateResponse {

    private static final String JSON_COMPONENT_VARIABLES = "variables";
    private static final String JSON_COMPONENT_BUILT_VARIABLES = "built_variables";

    private final Map<String, CampaignComponentVariableResponse> variables;
    private final Map<String, BuiltCampaignComponentVariableResponse> builtVariables;

    @JsonCreator
    public BatchVariableUpdateResponse(
        @JsonProperty(JSON_COMPONENT_VARIABLES) Map<String, CampaignComponentVariableResponse> variables,
        @JsonProperty(JSON_COMPONENT_BUILT_VARIABLES) Map<String,
            BuiltCampaignComponentVariableResponse> builtVariables) {
        this.variables = variables;
        this.builtVariables = builtVariables;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLES)
    public Map<String, CampaignComponentVariableResponse> getVariables() {
        return variables;
    }

    @JsonProperty(JSON_COMPONENT_BUILT_VARIABLES)
    public Map<String, BuiltCampaignComponentVariableResponse> getBuiltVariables() {
        return builtVariables;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
