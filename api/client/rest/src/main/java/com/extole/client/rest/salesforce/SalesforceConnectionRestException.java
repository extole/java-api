package com.extole.client.rest.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SalesforceConnectionRestException extends ExtoleRestException {

    public static final ErrorCode<SalesforceConnectionRestException> SALESFORCE_AUTHENTICATION_ERROR =
        new ErrorCode<>("salesforce_authentication_error", 400,
            "Authentication error occurred while trying to obtain an access token from Salesforce via oAuth service",
            "client_id");

    public static final ErrorCode<SalesforceConnectionRestException> SALESFORCE_AUTHORIZATION_ERROR =
        new ErrorCode<>("salesforce_authorization_error", 400,
            "Salesforce authorization error - client access token rejected by the Data API",
            "client_id");

    public static final ErrorCode<SalesforceConnectionRestException> SALESFORCE_SERVICE_UNAVAILABLE =
        new ErrorCode<>("salesforce_service_unavailable", 400,
            "Salesforce service unavailable",
            "client_id");

    public static final ErrorCode<SalesforceConnectionRestException> SALESFORCE_SETTINGS_DISABLED =
        new ErrorCode<>("salesforce_settings_disabled", 400,
            "Salesforce settings disabled",
            "client_id");

    public SalesforceConnectionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
