package com.extole.email.rest.audit;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailSendAddressRestException extends ExtoleRestException {
    public static final ErrorCode<EmailSendAddressRestException> EMAIL_ADDRESS_INVALID =
        new ErrorCode<>("email_address_invalid", 403, "Email address not valid", "address");

    public EmailSendAddressRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
