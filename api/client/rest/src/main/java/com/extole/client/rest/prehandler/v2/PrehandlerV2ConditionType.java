package com.extole.client.rest.prehandler.v2;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum PrehandlerV2ConditionType {

    EMAIL_DOMAIN_MATCH,
    NORMALIZED_EMAIL_MATCH,
    ORIGIN_MATCH,
    JAVASCRIPT,
    EVENT_NAME

}
