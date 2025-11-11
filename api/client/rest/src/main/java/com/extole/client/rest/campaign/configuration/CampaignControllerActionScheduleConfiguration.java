package com.extole.client.rest.campaign.configuration;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionScheduleConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_SCHEDULE_NAME = "schedule_name";
    private static final String JSON_DELAYS = "delays";
    private static final String JSON_DATES = "dates";
    private static final String JSON_FORCE = "force";
    private static final String JSON_DATA = "data";
    private static final String JSON_EXTRA_DATA = "extra_data";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, String>> scheduleName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> delays;
    private final List<ZonedDateTime> dates;
    private final boolean force;
    private final Map<
        BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
        BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> data;
    private final BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, Map<String, Optional<Object>>>> extraData;

    public CampaignControllerActionScheduleConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_SCHEDULE_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, String>> scheduleName,
        @JsonProperty(JSON_DELAYS) BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> delays,
        @JsonProperty(JSON_DATES) List<ZonedDateTime> dates,
        @JsonProperty(JSON_FORCE) boolean force,
        @JsonProperty(JSON_DATA) Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> data,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(JSON_EXTRA_DATA) BuildtimeEvaluatable<
            ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, Map<String, Optional<Object>>>> extraData) {
        super(actionId, CampaignControllerActionType.SCHEDULE, quality, enabled, componentReferences);
        this.scheduleName = scheduleName;
        this.force = force;
        this.delays = delays;
        this.dates = dates;
        this.data = data != null ? ImmutableMap.copyOf(data) : ImmutableMap.of();
        this.extraData = extraData;
    }

    @JsonProperty(JSON_SCHEDULE_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ScheduleActionContext, String>>
        getScheduleName() {
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
    public
        Map<BuildtimeEvaluatable<ControllerBuildtimeContext, String>,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>>
        getData() {
        return data;
    }

    @JsonProperty(JSON_EXTRA_DATA)
    public BuildtimeEvaluatable<
        ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, Map<String, Optional<Object>>>>
        getExtraData() {
        return extraData;
    }

}
