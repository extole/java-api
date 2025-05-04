package com.extole.client.rest.schedule;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ScheduleResponse {
    private static final String JSON_SCHEDULE_ID = "schedule_id";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_CONTROLLER_ACTION_ID = "controller_action_id";
    private static final String JSON_PERSON_ID = "person_id";
    private static final String JSON_PROGRAM_ID = "program_id";
    private static final String JSON_SCHEDULE_NAME = "schedule_name";
    private static final String JSON_CONTAINER = "container";
    private static final String JSON_STATUS = "status";
    private static final String JSON_CREATED_DATE = "created_date";
    private static final String JSON_SCHEDULE_START_DATE = "schedule_start_date";
    private static final String JSON_SCHEDULED_EXECUTION_DATE = "scheduled_execution_date";
    private static final String JSON_DATA = "data";

    private final String scheduleId;
    private final String campaignId;
    private final String controllerActionId;
    private final String personId;
    private final String programId;
    private final String scheduleName;
    private final String container;
    private final ScheduleStatus status;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime scheduleStartDate;
    private final ZonedDateTime scheduledExecutionDate;
    private final Map<String, String> data;

    public ScheduleResponse(
        @JsonProperty(JSON_SCHEDULE_ID) String scheduleId,
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CONTROLLER_ACTION_ID) String controllerActionId,
        @JsonProperty(JSON_PERSON_ID) String personId,
        @JsonProperty(JSON_PROGRAM_ID) String programId,
        @JsonProperty(JSON_SCHEDULE_NAME) String scheduleName,
        @JsonProperty(JSON_CONTAINER) String container,
        @JsonProperty(JSON_STATUS) ScheduleStatus status,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_SCHEDULE_START_DATE) ZonedDateTime scheduleStartDate,
        @JsonProperty(JSON_SCHEDULED_EXECUTION_DATE) ZonedDateTime scheduledExecutionDate,
        @JsonProperty(JSON_DATA) Map<String, String> data) {
        this.scheduleId = scheduleId;
        this.campaignId = campaignId;
        this.controllerActionId = controllerActionId;
        this.personId = personId;
        this.programId = programId;
        this.scheduleName = scheduleName;
        this.container = container;
        this.status = status;
        this.createdDate = createdDate;
        this.scheduleStartDate = scheduleStartDate;
        this.scheduledExecutionDate = scheduledExecutionDate;
        this.data = data;
    }

    @JsonProperty(JSON_SCHEDULE_ID)
    public String getScheduleId() {
        return this.scheduleId;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return this.campaignId;
    }

    @JsonProperty(JSON_CONTROLLER_ACTION_ID)
    public String getControllerActionId() {
        return this.controllerActionId;
    }

    @JsonProperty(JSON_PERSON_ID)
    public String getPersonId() {
        return this.personId;
    }

    @JsonProperty(JSON_PROGRAM_ID)
    public String getProgramId() {
        return this.programId;
    }

    @JsonProperty(JSON_SCHEDULE_NAME)
    public String getScheduleName() {
        return this.scheduleName;
    }

    @JsonProperty(JSON_CONTAINER)
    public String getContainer() {
        return this.container;
    }

    @JsonProperty(JSON_STATUS)
    public ScheduleStatus getStatus() {
        return this.status;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    @JsonProperty(JSON_SCHEDULE_START_DATE)
    public ZonedDateTime getScheduleStartDate() {
        return this.scheduleStartDate;
    }

    @JsonProperty(JSON_SCHEDULED_EXECUTION_DATE)
    public ZonedDateTime getScheduledExecutionDate() {
        return this.scheduledExecutionDate;
    }

    @JsonProperty(JSON_DATA)
    public Map<String, String> getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
