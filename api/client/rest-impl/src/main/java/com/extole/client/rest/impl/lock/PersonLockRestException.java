package com.extole.client.rest.impl.lock;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonLockRestException extends ExtoleRestException {

    public static final ErrorCode<PersonLockRestException> LOCK_SERVICE_UNAVAILABLE =
        new ErrorCode<>("lock_service_unavailable", 503, "Error retrieving lock.", "message");

    public PersonLockRestException(String uniqueId, ErrorCode<PersonLockRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
