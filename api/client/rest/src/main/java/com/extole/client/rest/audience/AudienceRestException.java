package com.extole.client.rest.audience;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceRestException> AUDIENCE_NOT_FOUND = new ErrorCode<>(
        "audience_not_found", 400, "Audience was not found", "audience_id");

    public AudienceRestException(String uniqueId, ErrorCode<AudienceRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
