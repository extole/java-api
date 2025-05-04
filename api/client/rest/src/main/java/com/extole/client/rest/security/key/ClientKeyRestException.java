package com.extole.client.rest.security.key;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientKeyRestException extends ExtoleRestException {

    public static final ErrorCode<ClientKeyRestException> CLIENT_KEY_NOT_FOUND = new ErrorCode<>(
        "client_key_not_found", 400, "Key is not found", "key_id");

    public static final ErrorCode<ClientKeyRestException> PGP_EXTOLE_CLIENT_KEY_NOT_FOUND = new ErrorCode<>(
        "pgp_extole_client_key_not_found", 400, "PGP Extole key is not found");

    public ClientKeyRestException(String uniqueId, ErrorCode<ClientKeyRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
