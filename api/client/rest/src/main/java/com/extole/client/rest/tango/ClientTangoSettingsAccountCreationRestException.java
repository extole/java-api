package com.extole.client.rest.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientTangoSettingsAccountCreationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientTangoSettingsAccountCreationRestException> ACCOUNT_LIMIT_REACHED =
        new ErrorCode<>("account_limit_reached", 400,
            "Client already has the maximum number of tango accounts", "account_limit");

    public static final ErrorCode<ClientTangoSettingsAccountCreationRestException> INVALID_FUNDS_AMOUNT_WARN_LIMIT =
        new ErrorCode<>("funds_amount_warn_limit_invalid", 400,
            "Funds amount warn limit must be a non-negative integer",
            "funds_amount_warn_limit");

    public static final ErrorCode<ClientTangoSettingsAccountCreationRestException> MISSING_FUNDS_AMOUNT_WARN_LIMIT =
        new ErrorCode<>("missing_funds_amount_warn_limit", 400,
            "Funds amount warn limit must be defined");

    public ClientTangoSettingsAccountCreationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
