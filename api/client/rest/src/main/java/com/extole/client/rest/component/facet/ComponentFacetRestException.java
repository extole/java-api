package com.extole.client.rest.component.facet;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentFacetRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentFacetRestException> COMPONENT_FACET_NOT_FOUND = new ErrorCode<>(
        "component_facet_not_found", 400, "Component facet was not found", "name");

    public static final ErrorCode<ComponentFacetRestException> FACET_ASSOCIATED_WITH_COMPONENTS =
        new ErrorCode<>("facet_associated_with_components", 400, "Component facet is associated with components",
            "facet_name", "component_ids");

    public ComponentFacetRestException(String uniqueId, ErrorCode<ComponentFacetRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
