package com.extole.client.rest.campaign.controller.action.schedule;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionScheduleResponse extends CampaignControllerActionResponse {
    private static final String JSON_SCHEDULE_NAME = "schedule_name";
    private static final String JSON_DELAYS = "delays";
    private static final String JSON_DATES = "dates";
    private static final String JSON_FORCE = "force";
    private static final String JSON_DATA = "data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> scheduleName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> delays;
    private final List<ZonedDateTime> dates;
    private final boolean force;
    private final Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> data;

    public CampaignControllerActionScheduleResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SCHEDULE_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> scheduleName,
        @JsonProperty(JSON_DELAYS) BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> delays,
        @JsonProperty(JSON_DATES) List<ZonedDateTime> dates,
        @JsonProperty(JSON_FORCE) boolean force,
        @JsonProperty(JSON_DATA) Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.SCHEDULE, quality, enabled, componentIds, componentReferences);
        this.scheduleName = scheduleName;
        this.force = force;
        this.delays = delays;
        this.dates = dates;
        this.data = data;
    }

    @JsonProperty(JSON_SCHEDULE_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getScheduleName() {
        return scheduleName;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty(JSON_DELAYS)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> getDelays() {
        return delays;
    }

    @JsonProperty(JSON_DATES)
    public List<ZonedDateTime> getDates() {
        return this.dates;
    }

    @JsonProperty(JSON_FORCE)
    public boolean isForce() {
        return force;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> getData() {
        return data;
    }

}
