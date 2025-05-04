package com.extole.client.rest.component.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentTypeValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentTypeValidationRestException> MISSING_COMPONENT_TYPE_NAME = new ErrorCode<>(
        "missing_component_type_name", 400, "Component type name is not specified");

    public static final ErrorCode<ComponentTypeValidationRestException> INVALID_COMPONENT_TYPE_NAME_LENGTH =
        new ErrorCode<>("invalid_component_type_name_length", 400, "Component type name length is invalid", "name",
            "min_length", "max_length");

    public static final ErrorCode<
        ComponentTypeValidationRestException> COMPONENT_TYPE_NAME_CONTAINS_ILLEGAL_CHARACTERS =
            new ErrorCode<>("component_type_name_contains_illegal_characters", 400,
                "Component type name should contain alphanumeric, dot, underscore and dash characters only", "name");

    public static final ErrorCode<ComponentTypeValidationRestException> INVALID_COMPONENT_TYPE_DISPLAY_NAME_LENGTH =
        new ErrorCode<>("invalid_component_type_display_name_length", 400,
            "Component type display name length is invalid", "display_name", "min_length", "max_length");

    public static final ErrorCode<
        ComponentTypeValidationRestException> COMPONENT_TYPE_DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTERS = new ErrorCode<>(
            "component_type_display_name_contains_illegal_characters", 400,
            "Component type display name should contain alphanumeric, dash, underscore, whitespace, asterisk, " +
                "hash, dollar characters only",
            "display_name");

    public static final ErrorCode<ComponentTypeValidationRestException> COMPONENT_TYPE_NAME_ALREADY_USED =
        new ErrorCode<>(
            "component_type_name_already_used", 400, "Component type name is already used", "name");

    public static final ErrorCode<ComponentTypeValidationRestException> MISSING_COMPONENT_TYPE_SCHEMA = new ErrorCode<>(
        "missing_component_type_schema", 400, "Component type schema is not specified");

    public static final ErrorCode<ComponentTypeValidationRestException> INVALID_COMPONENT_TYPE_PARENT = new ErrorCode<>(
        "invalid_component_type_parent", 400, "Invalid component type parent", "parent");

    public static final ErrorCode<ComponentTypeValidationRestException> INVALID_COMPONENT_TYPE_SCHEMA = new ErrorCode<>(
        "invalid_component_type_schema", 400, "Invalid component type schema", "schema");

    public ComponentTypeValidationRestException(String uniqueId, ErrorCode<ComponentTypeValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
