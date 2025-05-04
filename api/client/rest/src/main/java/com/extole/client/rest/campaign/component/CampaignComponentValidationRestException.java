package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignComponentValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignComponentValidationRestException> INVALID_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "invalid_component_reference", 400, "Unknown referenced campaign component", "identifier_type",
            "identifier");

    public static final ErrorCode<CampaignComponentValidationRestException> INVALID_EXTERNAL_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "invalid_external_component_reference", 400, "Unknown external referenced campaign component",
            "identifier_type", "identifier");

    public static final ErrorCode<CampaignComponentValidationRestException> ORPHAN_EXTERNAL_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "orphan_external_component_reference", 400, "Orphan external referenced campaign component",
            "reference_owner_campaign_id", "reference_owner_campaign_version", "missing_campaign_component_id");

    public static final ErrorCode<CampaignComponentValidationRestException> EXCESSIVE_ROOT_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "excessive_component_reference", 400, "Root campaign component may have at most one reference",
            "identifier_type", "identifier");

    public static final ErrorCode<CampaignComponentValidationRestException> SELF_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "self_component_reference", 400, "Self campaign component reference is not allowed",
            "identifier_type", "identifier");

    public static final ErrorCode<CampaignComponentValidationRestException> REDUNDANT_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "redundant_component_reference", 400, "Same reference defined multiple times",
            "referenced_component_name", "referencing_entity_type", "referencing_entity");

    public static final ErrorCode<CampaignComponentValidationRestException> CIRCULAR_COMPONENT_REFERENCE =
        new ErrorCode<>(
            "circular_component_reference", 400, "Circular campaign component reference is not allowed",
            "cycles");

    public static final ErrorCode<CampaignComponentValidationRestException> NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_name_out_of_range", 400,
            "Campaign component name length is out of range", "name", "min_length", "max_length");

    public static final ErrorCode<CampaignComponentValidationRestException> DISPLAY_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_display_name_out_of_range", 400,
            "Campaign component display name length is out of range", "display_name", "min_length", "max_length");

    public static final ErrorCode<CampaignComponentValidationRestException> DESCRIPTION_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("campaign_component_description_out_of_range", 400,
            "Campaign component description length is out of range", "max_length");

    public static final ErrorCode<CampaignComponentValidationRestException> NAME_ALREADY_IN_USE =
        new ErrorCode<>("campaign_component_name_already_in_use", 400,
            "Campaign component name is already used by another component", "name");

    public static final ErrorCode<CampaignComponentValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_component_name_contains_illegal_character", 400,
            "Campaign component name can only contain alphanumeric, dash and underscore characters", "name");

    public static final ErrorCode<CampaignComponentValidationRestException> DISPLAY_NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("campaign_component_display_name_contains_illegal_character", 400,
            "Campaign component display name can only contain alphanumeric, dash, underscore, whitespace, asterisk, " +
                "hash, dollar characters",
            "display_name");
    public static final ErrorCode<CampaignComponentValidationRestException> REFERENCE_ABSOLUTE_NAME_MISSING =
        new ErrorCode<>("campaign_component_absolute_name_missing", 400, "Campaign component absolute name is missing");

    public static final ErrorCode<CampaignComponentValidationRestException> REFERENCE_COMPONENT_ID_MISSING =
        new ErrorCode<>("campaign_component_id_missing", 400, "Campaign component id is missing");

    public static final ErrorCode<
        CampaignComponentValidationRestException> EXTERNAL_ELEMENTS_CANNOT_HAVE_MULTIPLE_REFERENCES =
            new ErrorCode<>("external_elements_cannot_have_multiple_references", 400,
                "External elements cannot have multiple references");

    public static final ErrorCode<CampaignComponentValidationRestException> EXTERNAL_ELEMENT_IS_REFERENCED =
        new ErrorCode<>("external_element_is_referenced_by_active_configuration", 400,
            "External element is referenced by active configuration", "references", "element_type", "element_id");

    public static final ErrorCode<CampaignComponentValidationRestException> TYPE_VALIDATION_FAILED =
        new ErrorCode<>("campaign_component_type_validation_failed", 400, "Campaign component type validation failed",
            "validation_result", "name");

    public static final ErrorCode<CampaignComponentValidationRestException> INVALID_COMPONENT_REFERENCE_SOCKET_NAME =
        new ErrorCode<>("invalid_component_reference_socket_name", 400, "Invalid component reference socket name",
            "identifier_type", "identifier", "socket_name");

    public static final ErrorCode<CampaignComponentValidationRestException> INVALID_COMPONENT_INSTALLED_INTO_SOCKET =
        new ErrorCode<>("invalid_component_installed_into_socket", 400,
            "Invalid component installed into socket", "install_component_id", "socket_name");

    public CampaignComponentValidationRestException(String uniqueId,
        ErrorCode<CampaignComponentValidationRestException> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
