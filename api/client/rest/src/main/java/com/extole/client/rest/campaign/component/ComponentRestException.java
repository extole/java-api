package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentRestException> COMPONENT_NOT_FOUND = new ErrorCode<>(
        "component_not_found", 400, "Component not found", "component_id");

    public static final ErrorCode<ComponentRestException> UNSUPPORTED_OWNER = new ErrorCode<>(
        "component_owner_not_supported", 400, "Component owner not supported", "owner", "supported_values");

    public static final ErrorCode<ComponentRestException> UNSUPPORTED_STATE = new ErrorCode<>(
        "component_state_not_supported", 400, "Component state not supported", "state", "supported_values");

    public static final ErrorCode<ComponentRestException> UNKNOWN_SOURCE_CLIENT_IDS = new ErrorCode<>(
        "unknown_source_clients", 400, "Some source client ids are unknown", "known_source_client_ids",
        "unknown_source_client_ids");

    public ComponentRestException(String uniqueId, ErrorCode<ComponentRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
