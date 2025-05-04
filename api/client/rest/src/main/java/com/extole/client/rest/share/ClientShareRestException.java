package com.extole.client.rest.share;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientShareRestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareRestException> SHARE_NOT_FOUND =
        new ErrorCode<>("share_not_found", 400, "Share not found", "share_id");

    public ClientShareRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
