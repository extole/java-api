package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.signal.SignalActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionSignalConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DATA = "data";

    private final RuntimeEvaluatable<SignalActionContext, String> signalPollingId;
    private final RuntimeEvaluatable<SignalActionContext, String> name;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<SignalActionContext, Optional<Object>>>> data;

    public CampaignControllerActionSignalConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) RuntimeEvaluatable<SignalActionContext, String> signalPollingId,
        @JsonProperty(JSON_NAME) RuntimeEvaluatable<SignalActionContext, String> name,
        @JsonProperty(JSON_DATA) Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<SignalActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.SIGNAL, quality, enabled, componentReferences);
        this.signalPollingId = signalPollingId;
        this.name = name;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
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
    public
        Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<SignalActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

}
