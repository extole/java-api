package com.extole.client.rest.security.key.oauth.sfdc.password;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthSfdcPasswordClientKeyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<OAuthSfdcPasswordClientKeyValidationRestException> MISSING_USERNAME =
        new ErrorCode<>("missing_username", 400, "Missing username");

    public static final ErrorCode<OAuthSfdcPasswordClientKeyValidationRestException> MISSING_PASSWORD =
        new ErrorCode<>("missing_password", 400, "Missing password");

    public OAuthSfdcPasswordClientKeyValidationRestException(String uniqueId,
        ErrorCode<OAuthSfdcPasswordClientKeyValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
