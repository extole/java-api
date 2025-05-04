package com.extole.client.rest.prehandler.condition;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum PrehandlerConditionType {
    EVENT_NAME_MATCH,
    HTTP_HEADER_MATCH,
    JAVASCRIPT_V1,
    EXPRESSION,
    DATA_EXISTS,
    BLOCK_MATCH
}
