package com.extole.client.rest.component.facet;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ComponentFacetValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ComponentFacetValidationRestException> NAME_LENGTH_OUT_OF_RANGE = new ErrorCode<>(
        "component_facet_name_length_out_of_range", 400, "Component facet name length is out of range",
        "name", "min_length", "max_length");

    public static final ErrorCode<ComponentFacetValidationRestException> DISPLAY_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "component_facet_display_name_length_out_of_range", 400,
            "Component facet display name length is out of range",
            "display_name", "min_length", "max_length");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_DISPLAY_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "component_facet_value_display_name_length_out_of_range", 400,
            "Component facet value display name length is out of range",
            "display_name", "min_length", "max_length", "value");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_COLOR_INVALID =
        new ErrorCode<>(
            "component_facet_value_color_invalid", 400,
            "Component facet value color is invalid",
            "color", "value");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "component_facet_value_description_length_out_of_range", 400,
            "Component facet value description length is out of range",
            "description", "min_length", "max_length", "value");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_ICON_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "component_facet_value_icon_length_out_of_range", 400, "Component facet icon length is out of range",
            "icon", "min_length", "max_length", "value");

    public static final ErrorCode<ComponentFacetValidationRestException> NAME_IS_MISSING = new ErrorCode<>(
        "component_facet_name_is_missing", 400, "Component facet name is missing");

    public static final ErrorCode<ComponentFacetValidationRestException> NAME_ALREADY_EXISTS = new ErrorCode<>(
        "component_facet_name_already_exists", 400, "Component facet name already exists", "name");

    public static final ErrorCode<ComponentFacetValidationRestException> NAME_ILLEGAL_CHARACTER =
        new ErrorCode<>("component_facet_name_illegal_character", 400,
            "Component facet name can only contain alphanumeric, dash, underscore", "name");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_LENGTH_OUT_OF_RANGE = new ErrorCode<>(
        "component_facet_value_length_out_of_range", 400, "Component facet value length is out of range", "name",
        "value", "min_length", "max_length");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUES_SIZE_OUT_OF_RANGE = new ErrorCode<>(
        "component_facet_values_size_out_of_range", 400, "Component facet values size is out of range", "name",
        "max_size");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_ILLEGAL_CHARACTER =
        new ErrorCode<>("component_facet_value_illegal_character", 400,
            "Component facet name can only contain alphanumeric, dash, underscore", "value");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_DESCRIPTION_ILLEGAL_CHARACTER =
        new ErrorCode<>("component_facet_value_description_illegal_character", 400,
            "Component facet value description can only contain alphanumeric, dash, underscore", "description",
            "value");

    public static final ErrorCode<ComponentFacetValidationRestException> VALUE_ICON_ILLEGAL_CHARACTER =
        new ErrorCode<>("component_facet_value_icon_illegal_character", 400,
            "Component facet value icon can only contain alphanumeric, dash, underscore", "icon", "value");

    public ComponentFacetValidationRestException(String uniqueId, ErrorCode<ComponentFacetValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
