package com.extole.client.rest.security.key.oauth.lead.perfection;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthLeadPerfectionClientKeyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<
        OAuthLeadPerfectionClientKeyValidationRestException> MISSING_LEAD_PERFECTION_CLIENT_ID =
            new ErrorCode<>("missing_lead_perfection_client_id", 400, "Missing lead perfection client id");

    public static final ErrorCode<OAuthLeadPerfectionClientKeyValidationRestException> MISSING_APP_KEY =
        new ErrorCode<>("missing_app_key", 400, "Missing app key");

    public OAuthLeadPerfectionClientKeyValidationRestException(String uniqueId,
        ErrorCode<OAuthLeadPerfectionClientKeyValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
