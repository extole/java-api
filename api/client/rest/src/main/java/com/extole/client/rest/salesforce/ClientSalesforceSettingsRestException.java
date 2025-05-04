package com.extole.client.rest.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSalesforceSettingsRestException extends ExtoleRestException {

    public static final ErrorCode<ClientSalesforceSettingsRestException> SALESFORCE_SETTINGS_NOT_FOUND =
        new ErrorCode<>("salesforce_settings_not_defined", 400, "Salesforce settings not defined for this client",
            "client_id", "settings_id");

    public static final ErrorCode<ClientSalesforceSettingsRestException> SALESFORCE_SETTINGS_USED_BY_REWARD_SUPPLIERS =
        new ErrorCode<>("salesforce_settings_used_by_reward_suppliers", 400,
            "You can't delete this account, since it is associated with SFCC coupon reward suppliers. " +
                "Please delete your SFCC reward suppliers first.",
            "client_id", "reward_supplier_ids", "settings_id");

    public ClientSalesforceSettingsRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
