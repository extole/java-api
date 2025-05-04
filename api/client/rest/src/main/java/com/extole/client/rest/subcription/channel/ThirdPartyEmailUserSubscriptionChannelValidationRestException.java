package com.extole.client.rest.subcription.channel;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class ThirdPartyEmailUserSubscriptionChannelValidationRestException
    extends UserSubscriptionChannelValidationRestException {

    public static final ErrorCode<ThirdPartyEmailUserSubscriptionChannelValidationRestException> MISSING_RECIPIENT =
        new ErrorCode<>("missing_recipient", 400, "Recipient email is missing");
    public static final ErrorCode<ThirdPartyEmailUserSubscriptionChannelValidationRestException> INVALID_RECIPIENT =
        new ErrorCode<>("invalid_recipient", 400, "Recipient email is invalid", "recipient");
    public static final ErrorCode<ThirdPartyEmailUserSubscriptionChannelValidationRestException> RECIPIENT_IS_A_USER =
        new ErrorCode<>("recipient_is_existing_user", 400, "Recipient email belongs to an existing user", "recipient");

    public ThirdPartyEmailUserSubscriptionChannelValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
