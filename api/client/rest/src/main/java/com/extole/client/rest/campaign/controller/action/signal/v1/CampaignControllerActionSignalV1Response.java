package com.extole.client.rest.campaign.controller.action.signal.v1;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionSignalV1Response extends CampaignControllerActionResponse {
    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_DATA = "data";

    private final String signalPollingId;
    private final Map<String, String> data;

    public CampaignControllerActionSignalV1Response(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) String signalPollingId,
        @JsonProperty(JSON_DATA) Map<String, String> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.SIGNAL_V1, quality, enabled, componentIds, componentReferences);
        this.signalPollingId = signalPollingId;
        this.data = data;
    }

    @JsonProperty(JSON_SIGNAL_POLLING_ID)
    public String getSignalPollingId() {
        return signalPollingId;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return data;
    }

}
