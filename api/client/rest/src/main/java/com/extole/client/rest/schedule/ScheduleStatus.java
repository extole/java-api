package com.extole.client.rest.schedule;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ScheduleStatus {
    SCHEDULED, DONE, CANCELLED, REPLACED
}
