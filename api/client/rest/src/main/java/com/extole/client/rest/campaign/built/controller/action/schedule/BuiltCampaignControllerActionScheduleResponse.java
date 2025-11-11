package com.extole.client.rest.campaign.built.controller.action.schedule;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltCampaignControllerActionScheduleResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_SCHEDULE_NAME = "schedule_name";
    private static final String JSON_DELAYS = "delays";
    private static final String JSON_DATES = "dates";
    private static final String JSON_FORCE = "force";
    private static final String JSON_DATA = "data";
    private static final String JSON_EXTRA_DATA = "extra_data";

    private final RuntimeEvaluatable<ScheduleActionContext, String> scheduleName;
    private final List<Duration> delays;
    private final List<ZonedDateTime> dates;
    private final boolean force;
    private final Map<String, RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>> data;
    private final RuntimeEvaluatable<ScheduleActionContext, Map<String, Optional<Object>>> extraData;

    public BuiltCampaignControllerActionScheduleResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SCHEDULE_NAME) RuntimeEvaluatable<ScheduleActionContext, String> scheduleName,
        @JsonProperty(JSON_DELAYS) List<Duration> delays,
        @JsonProperty(JSON_DATES) List<ZonedDateTime> dates,
        @JsonProperty(JSON_FORCE) boolean force,
        @JsonProperty(JSON_DATA) Map<String, RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>> data,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_EXTRA_DATA) RuntimeEvaluatable<ScheduleActionContext,
            Map<String, Optional<Object>>> extraData) {
        super(actionId, CampaignControllerActionType.SCHEDULE, quality, enabled, componentIds, componentReferences);
        this.scheduleName = scheduleName;
        this.force = force;
        this.delays = delays != null ? delays : Collections.emptyList();
        this.dates = dates != null ? dates : Collections.emptyList();
        this.data = data != null ? data : Collections.emptyMap();
        this.extraData = extraData;
    }

    @JsonProperty(JSON_SCHEDULE_NAME)
    public RuntimeEvaluatable<ScheduleActionContext, String> getScheduleName() {
        return scheduleName;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty(JSON_DELAYS)
    public List<Duration> getDelays() {
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
    public Map<String, RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>> getData() {
        return data;
    }

    @JsonProperty(JSON_EXTRA_DATA)
    public RuntimeEvaluatable<ScheduleActionContext, Map<String, Optional<Object>>> getExtraData() {
        return extraData;
    }

}
