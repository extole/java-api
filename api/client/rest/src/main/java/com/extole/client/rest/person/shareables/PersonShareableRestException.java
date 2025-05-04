package com.extole.client.rest.person.shareables;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonShareableRestException extends ExtoleRestException {

    public static final ErrorCode<PersonShareableRestException> SHAREABLE_NOT_FOUND =
        new ErrorCode<>("shareable_not_found", 400, "Shareable not found", "person_id", "code");

    public PersonShareableRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
