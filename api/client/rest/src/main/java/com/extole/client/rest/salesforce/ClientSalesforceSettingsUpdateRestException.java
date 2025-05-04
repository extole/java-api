package com.extole.client.rest.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSalesforceSettingsUpdateRestException extends ExtoleRestException {

    public static final ErrorCode<ClientSalesforceSettingsUpdateRestException> ASSOCIATED_TO_REWARD_SUPPLIER =
        new ErrorCode<>("associated_to_reward_supplier", 400, "Salesforce settings can't be disabled for client,"
            + " since they are associated with Salesforce coupon reward suppliers",
            "client_id");

    public ClientSalesforceSettingsUpdateRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
