package com.extole.client.rest.campaign.component.setting;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SettingValidationRestException extends ExtoleRestException {

    public static final ErrorCode<SettingValidationRestException> EXPLICIT_VALUES_FOR_INHERITED_VARIABLE_NOT_ALLOWED =
        new ErrorCode<>("explicit_values_for_inherited_variable_not_allowed", 400,
            "Explicit values for inherited variable are not allowed");

    public static final ErrorCode<SettingValidationRestException> NAME_MISSING =
        new ErrorCode<>("setting_name_missing", 400, "Setting name is missing");

    public static final ErrorCode<SettingValidationRestException> NON_TRANSLATABLE_VARIABLE_CAN_NOT_BE_UPDATED =
        new ErrorCode<>("non_translatable_variable_can_not_be_updated", 400,
            "Only translatable variables are allowed for updated", "forbidden_variables");

    public static final ErrorCode<SettingValidationRestException> INVALID_TRANSLATABLE_VALUE =
        new ErrorCode<>("invalid_translatable_value", 400, "Translatable variable value is invalid",
            "variable_name", "variable_value_key", "variable_value");

    public static final ErrorCode<SettingValidationRestException> DEFAULT_KEY_NOT_ALLOWED =
        new ErrorCode<>("default_key_not_allowed", 400, "Default key cannot be defined for translatable variables",
            "variable_name", "variable_value_key", "variable_value");

    public static final ErrorCode<SettingValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("setting_name_length_out_of_range", 400,
            "Setting name length is out of range", "name", "min_length", "max_length");

    public static final ErrorCode<SettingValidationRestException> VARIABLE_VALUE_MISSING =
        new ErrorCode<>("variable_value_missing", 400,
            "Variable value missing", "name", "details");

    public static final ErrorCode<SettingValidationRestException> DISPLAY_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("setting_display_name_length_out_of_range", 400,
            "Setting display name length is out of range", "display_name", "min_length", "max_length");

    public static final ErrorCode<SettingValidationRestException> DISPLAY_NAME_HAS_ILLEGAL_CHARACTER =
        new ErrorCode<>("display_name_has_illegal_character", 400,
            "Setting display name contains illegal characters, it should contain alphanumeric, " +
                "space, dash, parenthesis, slash, colon, underscore, dollar, apostrophe and percentage only",
            "display_name");

    public static final ErrorCode<SettingValidationRestException> VALUE_KEY_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("variable_value_key_length_out_of_range", 400,
            "Variable value key length is out of range", "value_key", "min_length", "max_length");

    public static final ErrorCode<SettingValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("variable_description_length_out_of_range", 400,
            "Variable description length is out of range", "description", "max_length");

    public static final ErrorCode<SettingValidationRestException> TAG_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("tag_length_out_of_range", 400,
            "Setting tag length is out of range", "invalid_tag", "max_length", "min_length");

    public static final ErrorCode<SettingValidationRestException> DUPLICATED_NAME =
        new ErrorCode<>("setting_name_duplicated", 400,
            "Setting names should be unique", "name");

    public static final ErrorCode<SettingValidationRestException> RESERVED_NAME =
        new ErrorCode<>("setting_name_reserved", 400,
            "Setting name is a reserved keyword", "name");

    public static final ErrorCode<SettingValidationRestException> CIRCULAR_REFERENCE =
        new ErrorCode<>("variable_circular_reference", 400, "Variable circular reference", "component_id", "name",
            "key", "cyclic_references");

    public static final ErrorCode<SettingValidationRestException> UNAVAILABLE_REFERENCED_VARIABLE =
        new ErrorCode<>("unavailable_referenced_variable", 400, "Unavailable referenced variable",
            "campaign_id", "campaign_version", "component_id", "name",
            "unavailable_referenced_variable_name", "attempted_variants", "available_variants", "unavailability_cause");

    public static final ErrorCode<SettingValidationRestException> INVALID_VALUE_TYPE =
        new ErrorCode<>("variable_value_invalid_type", 400, "Variable value has invalid type", "component_id", "name",
            "key", "value", "expected_types", "details");

    public static final ErrorCode<SettingValidationRestException> SETTING_VALIDATION =
        new ErrorCode<>("setting_validation_exception", 400,
            "Failed to validate setting property", "name", "details");

    public static final ErrorCode<SettingValidationRestException> SOCKET_FILTER_COMPONENT_TYPE_MISSING =
        new ErrorCode<>("socket_filter_component_type_missing", 400, "Socket filter component type is missing");

    public static final ErrorCode<SettingValidationRestException> SOCKET_FILTER_INVALID_COMPONENT_TYPE =
        new ErrorCode<>("socket_filter_invalid_component_type", 400, "Socket filter component type is invalid",
            "component_type");

    public static final ErrorCode<SettingValidationRestException> SOCKET_FILTER_INVALID_COMPONENT_FACET =
        new ErrorCode<>("socket_filter_invalid_component_facet", 400, "Socket filter component facet is invalid",
            "facet_name", "facet_value");

    public static final ErrorCode<SettingValidationRestException> SOCKET_FILTER_COMPONENT_FACET_NAME_MISSING =
        new ErrorCode<>("socket_filter_component_facet_name_missing", 400,
            "Socket filter component facet name is missing");

    public static final ErrorCode<SettingValidationRestException> SOCKET_FILTER_COMPONENT_FACET_VALUE_MISSING =
        new ErrorCode<>("socket_filter_component_facet_value_missing", 400,
            "Socket filter component facet value is missing");

    public static final ErrorCode<SettingValidationRestException> SOCKET_MISSING_REQUIRED_PARAMETER =
        new ErrorCode<>("socket_missing_required_parameter", 400, "Socket is missing required parameter",
            "socket_parameter_name", "socket_parameter_type", "socket_name");

    public static final ErrorCode<SettingValidationRestException> MULTIPLE_COMPONENTS_INSTALLED_INTO_SINGLE_SOCKET =
        new ErrorCode<>("multiple_components_installed_into_single_socket", 400,
            "Multiple components are installed into a single socket",
            "socket_parameter_name", "socket_parameter_type", "socket_name", "target_component_id",
            "installed_component_ids");

    public static final ErrorCode<SettingValidationRestException> REWARD_SUPPLIER_ID_LIST_INVALID_CONFIGURATION =
        new ErrorCode<>("reward_supplier_id_list_invalid_configuration", 400,
            "Invalid value configured, allowed values are reward supplier ids that are configured under " +
                "the allowed_reward_supplier_ids variable",
            "campaign_id", "campaign_version", "evaluatable",
            "component_id", "name", "reward_supplier_id");

    public static final ErrorCode<SettingValidationRestException> NOT_ACCESSIBLE_COMPONENT_ID = new ErrorCode<>(
        "component_id_not_accessible", 400,
        "Component setting points to a non-existing or not accessible component id", "name", "details");

    public SettingValidationRestException(String uniqueId, ErrorCode<SettingValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
