package com.extole.client.rest.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSalesforceSettingsCreateRestException extends ExtoleRestException {

    public static final ErrorCode<ClientSalesforceSettingsCreateRestException> SALESFORCE_SETTINGS_ALREADY_DEFINED =
        new ErrorCode<>("salesforce_settings_already_defined", 400, "Salesforce settings already defined",
            "client_id");

    public static final ErrorCode<ClientSalesforceSettingsCreateRestException> SALESFORCE_SETTINGS_NAME_USED =
        new ErrorCode<>("salesforce_settings_name_already_in_use", 400, "Salesforce settings name already in use",
            "settings_id");

    public ClientSalesforceSettingsCreateRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
