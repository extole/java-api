package com.extole.client.rest.v0;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ReviewStatusCauseType {
    ADMIN_USER, BATCH_FILE, CONTROLLER, TRANSITION_RULE, REWARD_RULE, QUALITY_CHECK
}
