package com.extole.client.rest.security.key.oauth.salesforce;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthSalesforceClientKeyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<OAuthSalesforceClientKeyValidationRestException> INVALID_ACCOUNT_ID =
        new ErrorCode<>("invalid_account_id", 400, "Account id should be a positive number");

    public OAuthSalesforceClientKeyValidationRestException(String uniqueId,
        ErrorCode<OAuthSalesforceClientKeyValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
