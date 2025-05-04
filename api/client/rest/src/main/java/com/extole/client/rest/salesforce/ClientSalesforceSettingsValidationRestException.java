package com.extole.client.rest.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSalesforceSettingsValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> SETTINGS_NAME_INVALID =
        new ErrorCode<>("name_invalid", 400, "Name is mandatory, size should be between 1 and 255", "name");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> BASE_URI_MISSING =
        new ErrorCode<>("base_uri_missing", 400,
            "Salesforce base URI is missing",
            "client_id");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> SITE_ID_MISSING =
        new ErrorCode<>("site_id_missing", 400,
            "Salesforce site id is missing",
            "client_id");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> USERNAME_MISSING =
        new ErrorCode<>("username_missing", 400,
            "Salesforce user name is missing",
            "client_id");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> PASSWORD_MISSING =
        new ErrorCode<>("password_missing", 400,
            "Salesforce password is missing",
            "client_id");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> BASE_URI_NOT_A_VALID_URI =
        new ErrorCode<>("base_uri_not_a_valid_uri", 400,
            "Base URI is not a valid URI",
            "client_id", "base_uri");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> BASE_URI_INVALID_FORMAT =
        new ErrorCode<>("base_uri_invalid_format", 400,
            "Salesforce base URI has invalid format. Expected format: scheme://domain:port",
            "client_id", "base_uri");

    public static final ErrorCode<ClientSalesforceSettingsValidationRestException> INVALID_SITE_ID =
        new ErrorCode<>("invalid_site_id", 400, "Salesforce site id must be a valid URL path segment",
            "client_id", "site_id");

    public ClientSalesforceSettingsValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
