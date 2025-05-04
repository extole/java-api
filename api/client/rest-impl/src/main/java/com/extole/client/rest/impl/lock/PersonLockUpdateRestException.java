package com.extole.client.rest.impl.lock;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonLockUpdateRestException extends ExtoleRestException {

    public static final ErrorCode<PersonLockUpdateRestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 400, "Could not find person for id specified", "person_id");

    public static final ErrorCode<PersonLockUpdateRestException> CONCURRENT_MODIFICATION_DETECTED =
        new ErrorCode<>("concurrent_modification_detected", 400,
            "Closure to update the person ran too long, another user has modified the person", "person_id");

    public PersonLockUpdateRestException(String uniqueId, ErrorCode<PersonLockUpdateRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
