package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionSignalV1Configuration extends CampaignControllerActionConfiguration {
    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_DATA = "data";

    private final String signalPollingId;
    private final Map<String, String> data;

    public CampaignControllerActionSignalV1Configuration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) String signalPollingId,
        @JsonProperty(JSON_DATA) Map<String, String> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.SIGNAL_V1, quality, enabled, componentReferences);
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
