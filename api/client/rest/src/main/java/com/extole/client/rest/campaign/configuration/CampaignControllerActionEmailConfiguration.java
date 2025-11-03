package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.email.EmailActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionEmailConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_ZONE_NAME = "zone_name";
    private static final String JSON_DATA = "data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> zoneName;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<EmailActionContext, Optional<Object>>>> data;

    public CampaignControllerActionEmailConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_ZONE_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> zoneName,
        @JsonProperty(JSON_DATA) Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<EmailActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.EMAIL, quality, enabled, componentReferences);
        this.zoneName = zoneName;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
    }

    @JsonProperty(JSON_ZONE_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getZoneName() {
        return zoneName;
    }

    @JsonProperty(JSON_DATA)
    public
        Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<EmailActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

}
