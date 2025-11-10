package com.extole.client.rest.test;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ProcessEventStatus {
    SUCCESSFULLY_SENT,
    IGNORED_EMPTY_LINE,
    INVALID_JSON,
    EVENT_TOO_LARGE,
    FAILED
}
