package com.extole.client.rest.component.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentTypeRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentTypeRestException> COMPONENT_TYPE_NOT_FOUND = new ErrorCode<>(
        "component_type_not_found", 400, "Component type was not found", "name");

    public ComponentTypeRestException(String uniqueId, ErrorCode<ComponentTypeRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
