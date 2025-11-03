package com.extole.client.rest.campaign.controller.action.schedule;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.schedule.ScheduleActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerActionScheduleCreateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_SCHEDULE_NAME = "schedule_name";
    private static final String JSON_DELAYS = "delays";
    private static final String JSON_DATES = "dates";
    private static final String JSON_FORCE = "force";
    private static final String JSON_DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, String>>> scheduleName;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>>> delays;
    private final Omissible<List<ZonedDateTime>> dates;
    private final Omissible<Boolean> force;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>>> data;

    @JsonCreator
    public CampaignControllerActionScheduleCreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_SCHEDULE_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, String>>> scheduleName,
        @JsonProperty(JSON_DELAYS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>>> delays,
        @JsonProperty(JSON_DATES) Omissible<List<ZonedDateTime>> dates,
        @JsonProperty(JSON_FORCE) Omissible<Boolean> force,
        @JsonProperty(JSON_DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.scheduleName = scheduleName;
        this.delays = delays;
        this.dates = dates;
        this.force = force;
        this.data = data;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_SCHEDULE_NAME)
    public
        Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, RuntimeEvaluatable<ScheduleActionContext, String>>>
        getScheduleName() {
        return scheduleName;
    }

    @JsonProperty(JSON_DELAYS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>>> getDelays() {
        return delays;
    }

    @JsonProperty(JSON_DATES)
    public Omissible<List<ZonedDateTime>> getDates() {
        return dates;
    }

    @JsonProperty(JSON_FORCE)
    public Omissible<Boolean> isForce() {
        return force;
    }

    @JsonProperty(JSON_DATA)
    public
        Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>>>
        getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<ScheduleActionContext, String>>> scheduleName =
                Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>>> delays =
            Omissible.omitted();
        private Omissible<List<ZonedDateTime>> dates = Omissible.omitted();
        private Omissible<Boolean> force = Omissible.omitted();
        private Omissible<Map<String,
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>>> data =
                    Omissible.omitted();

        private Builder() {
        }

        public Builder withScheduleName(String scheduleName) {
            this.scheduleName = Omissible.of(Provided.nestedOf(scheduleName));
            return this;
        }

        public Builder withScheduleName(
            BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, String>> scheduleName) {
            this.scheduleName = Omissible.of(scheduleName);
            return this;
        }

        public Builder withDelays(BuildtimeEvaluatable<ControllerBuildtimeContext, List<Duration>> delays) {
            this.delays = Omissible.of(delays);
            return this;
        }

        public Builder withDates(List<ZonedDateTime> dates) {
            this.dates = Omissible.of(dates);
            return this;
        }

        public Builder withForce(Boolean force) {
            this.force = Omissible.of(force);
            return this;
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withData(
            Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
                RuntimeEvaluatable<ScheduleActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        @Override
        public CampaignControllerActionScheduleCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionScheduleCreateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                scheduleName,
                delays,
                dates,
                force,
                data);
        }

    }

}
