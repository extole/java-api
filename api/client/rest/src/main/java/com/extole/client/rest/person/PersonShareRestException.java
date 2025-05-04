package com.extole.client.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonShareRestException extends ExtoleRestException {

    public static final ErrorCode<PersonShareRestException> SHARE_NOT_FOUND =
        new ErrorCode<>("share_not_found", 400, "Share not found", "share_id", "person_id");

    public PersonShareRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
