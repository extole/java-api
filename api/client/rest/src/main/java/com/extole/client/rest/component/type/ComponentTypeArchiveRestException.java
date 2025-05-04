package com.extole.client.rest.component.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentTypeArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentTypeArchiveRestException> PARENT_ASSOCIATED_WITH_COMPONENT_TYPE =
        new ErrorCode<>("parent_associated_with_component_type", 400,
            "Can't archive a parent component type associated with component types", "parent",
            "associated_component_types");

    public ComponentTypeArchiveRestException(String uniqueId, ErrorCode<ComponentTypeArchiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
