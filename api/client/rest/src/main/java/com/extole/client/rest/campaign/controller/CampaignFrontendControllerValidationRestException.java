package com.extole.client.rest.campaign.controller;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFrontendControllerValidationRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFrontendControllerValidationRestException> NOT_FOUND_PAGE_INVALID_ACTION =
        new ErrorCode<>("not_found_page_invalid_action", 400,
            "Frontend controller for not found page must have only one action of display type with ALWAYS quality",
            "campaign_id", "controller_id");

    public static final ErrorCode<
        CampaignFrontendControllerValidationRestException> NOT_FOUND_PAGE_FORBIDDEN_RUNTIME_EXPRESSIONS =
            new ErrorCode<>("not_found_page_forbidden_runtime_expressions", 400,
                "Frontend controller for not found page shouldn't use runtime expressions in the action",
                "campaign_id", "controller_id", "action_id");

    public static final ErrorCode<CampaignFrontendControllerValidationRestException> CONTROLLER_MISCONFIGURATION =
        new ErrorCode<>("controller_misconfiguration", 400,
            "A controller is not properly configured", "controller_id", "campaign_id", "details");

    public static final ErrorCode<CampaignFrontendControllerValidationRestException> DUPLICATE_FRONTEND_CONTROLLER =
        new ErrorCode<>("campaign_frontend_controller_duplicate_controller", 400,
            "Two frontend controllers have the same unique key (controller name, journey name)",
            "controller_name", "second_controller_id", "journey_names");

    public static final ErrorCode<
        CampaignFrontendControllerValidationRestException> CONTROLLER_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("controller_name_length_out_of_range", 400,
                "Campaign frontend controller name is not of valid length", "evaluatable", "controller_name",
                "min_length", "max_length");

    public static final ErrorCode<CampaignFrontendControllerValidationRestException> RESERVED_NAME =
        new ErrorCode<>("reserved_name", 400,
            "Campaign frontend controller name is reserved", "evaluatable", "controller_name");

    public static final ErrorCode<CampaignFrontendControllerValidationRestException> NAME_CONTAINS_ILLEGAL_CHARACTER =
        new ErrorCode<>("name_contains_illegal_character", 400,
            "Name should contain alphanumeric, underscore, dash, dot and space characters only",
            "evaluatable", "controller_name");

    public CampaignFrontendControllerValidationRestException(String uniqueId,
        ErrorCode<CampaignFrontendControllerValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
