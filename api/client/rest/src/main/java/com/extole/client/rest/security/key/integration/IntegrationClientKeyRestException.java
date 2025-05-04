package com.extole.client.rest.security.key.integration;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class IntegrationClientKeyRestException extends ExtoleRestException {

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_MISSING_CODE =
        new ErrorCode<>(
            "integration_missing_code", 400, "Code is a required parameter");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_MISSING_DESCRIPTION =
        new ErrorCode<>(
            "integration_missing_description", 400, "Description is mandatory");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_MISSING_NAME =
        new ErrorCode<>(
            "integration_missing_name", 400, "Name is mandatory");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_MISSING_TAGS =
        new ErrorCode<>(
            "integration_missing_tags", 400, "At least one tag is required");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_MISSING_TYPE =
        new ErrorCode<>(
            "integration_missing_type", 400, "Integration type is required");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_NOT_SUPPORTED =
        new ErrorCode<>(
            "integration_type_not_supported", 400, "Integration type not supported", "message");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_NOT_FOUND =
        new ErrorCode<>(
            "integration_not_found", 400, "Integration not found", "message");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_IN_USE =
        new ErrorCode<>(
            "integration_in_use", 400, "Resources associated with current integration are in use",
            "associated_webhook_action_ids");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_SETUP_FAILED =
        new ErrorCode<>(
            "integration_setup_failed", 400, "Integration setup failed", "message");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_INVALID_CODE =
        new ErrorCode<>(
            "integration_invalid_code", 400, "Integration failed, invalid code");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_INVALID_REDIRECT_URL =
        new ErrorCode<>(
            "integration_invalid_redirect_url", 400, "Integration failed, invalid redirect url");

    public static final ErrorCode<IntegrationClientKeyRestException> INTEGRATION_INVALID_APP_AUTHENTICATION =
        new ErrorCode<>(
            "integration_invalid_app_authentication", 400, "Integration failed, app authentication failed");

    public IntegrationClientKeyRestException(String uniqueId, ErrorCode<IntegrationClientKeyRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
