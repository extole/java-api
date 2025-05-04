package com.extole.client.rest.flow;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class FlowRestException extends ExtoleRestException {

    public static final ErrorCode<FlowRestException> MIXED_STEP_INCLUDE_AND_EXCLUDE_FILTERS = new ErrorCode<>(
        "flow_mixed_step_include_and_exclude_filters", 400,
        "Mixing of step include and exclude filters is now allowed");

    public static final ErrorCode<FlowRestException> CAMPAIGN_STATE_INVALID = new ErrorCode<>(
        "flow_campaign_state_invalid", 400, "Campaign state invalid. Expected: published or latest", "state");

    public static final ErrorCode<FlowRestException> PROGRAM_LABEL_NOT_FOUND =
        new ErrorCode<>("flow_program_label_not_found", 400, "Program label not found", "program_label");

    public static final ErrorCode<FlowRestException> MIXED_CAMPAIGN_ID_AND_PROGRAM_LABEL_FILTERS =
        new ErrorCode<>("flow_mixed_campaign_id_and_program_label_filters", 400,
            "Mixing of campaign id and program label filters is not allowed");

    public FlowRestException(String uniqueId, ErrorCode<FlowRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
