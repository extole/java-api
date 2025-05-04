package com.extole.client.rest.prehandler.action;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum PrehandlerActionType {
    SET_DATA,
    SET_SANDBOX,
    MAP_DATA_ATTRIBUTES,
    JAVASCRIPT_V1,
    EXPRESSION
}
