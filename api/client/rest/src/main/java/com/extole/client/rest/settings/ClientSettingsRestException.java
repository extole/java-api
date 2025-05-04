package com.extole.client.rest.settings;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSettingsRestException extends ExtoleRestException {

    public static final ErrorCode<ClientSettingsRestException> TIME_ZONE_INVALID = new ErrorCode<>("time_zone_invalid",
        400, "Invalid value for client time zone", "client_id", "time_zone");

    public ClientSettingsRestException(String uniqueId, ErrorCode<ClientSettingsRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
