package com.extole.client.rest.campaign.built.controller.action.signal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.action.signal.SignalActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionSignalResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DATA = "data";

    private final RuntimeEvaluatable<SignalActionContext, String> signalPollingId;
    private final RuntimeEvaluatable<SignalActionContext, String> name;
    private final Map<String, RuntimeEvaluatable<SignalActionContext, Optional<Object>>> data;

    public BuiltCampaignControllerActionSignalResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) RuntimeEvaluatable<SignalActionContext, String> signalPollingId,
        @JsonProperty(JSON_NAME) RuntimeEvaluatable<SignalActionContext, String> name,
        @JsonProperty(JSON_DATA) Map<String, RuntimeEvaluatable<SignalActionContext, Optional<Object>>> data,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.SIGNAL, quality, enabled, componentIds, componentReferences);
        this.signalPollingId = signalPollingId;
        this.name = name;
        this.data = data != null ? data : Collections.emptyMap();
    }

    @JsonProperty(JSON_SIGNAL_POLLING_ID)
    public RuntimeEvaluatable<SignalActionContext, String> getSignalPollingId() {
        return signalPollingId;
    }

    @JsonProperty(JSON_NAME)
    public RuntimeEvaluatable<SignalActionContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, RuntimeEvaluatable<SignalActionContext, Optional<Object>>> getData() {
        return data;
    }

}
