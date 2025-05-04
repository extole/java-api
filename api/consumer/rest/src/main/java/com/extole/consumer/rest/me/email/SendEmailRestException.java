package com.extole.consumer.rest.me.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SendEmailRestException extends ExtoleRestException {
    public static final ErrorCode<SendEmailRestException> PROFILE_NOT_SET =
        new ErrorCode<>("profile_not_set", 403, "Profile must have an associated email", "person_id");

    public static final ErrorCode<SendEmailRestException> MISSING_ZONE_NAME =
        new ErrorCode<>("missing_zone_name", 403, "Missing zone name");

    public static final ErrorCode<SendEmailRestException> INVALID_ZONE_NAME =
        new ErrorCode<>("invalid_zone_name", 403, "Invalid zone name (not an EMAIL zone)", "zone_name");

    public static final ErrorCode<SendEmailRestException> MISSING_CAMPAIGN_ID =
        new ErrorCode<>("missing_campaign_id", 403, "Campaign must be provided");

    public SendEmailRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
