package com.extole.client.rest.campaign.built.controller.action.email;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.action.email.EmailActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionEmailResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_ZONE_NAME = "zone_name";
    private static final String JSON_DATA = "data";

    private final String zoneName;
    private final Map<String, RuntimeEvaluatable<EmailActionContext, Optional<Object>>> data;

    public BuiltCampaignControllerActionEmailResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ZONE_NAME) String zoneName,
        @JsonProperty(JSON_DATA) Map<String, RuntimeEvaluatable<EmailActionContext, Optional<Object>>> data,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.EMAIL, quality, enabled, componentIds, componentReferences);
        this.zoneName = zoneName;
        this.data = data != null ? data : Collections.emptyMap();
    }

    @JsonProperty(JSON_ZONE_NAME)
    public String getZoneName() {
        return zoneName;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, RuntimeEvaluatable<EmailActionContext, Optional<Object>>> getData() {
        return data;
    }

}
