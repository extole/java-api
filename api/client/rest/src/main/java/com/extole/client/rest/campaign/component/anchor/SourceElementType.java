package com.extole.client.rest.campaign.component.anchor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum SourceElementType {
    ACTION, TRIGGER, MAPPING, MAPPING_STEP_DATA, CONTROLLER_STEP_DATA, FLOW_STEP_METRIC, FLOW_STEP_APP
}
