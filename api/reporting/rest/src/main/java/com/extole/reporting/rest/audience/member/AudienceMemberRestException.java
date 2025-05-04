package com.extole.reporting.rest.audience.member;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AudienceMemberRestException extends ExtoleRestException {

    public static final ErrorCode<AudienceMemberRestException> AUDIENCE_NOT_FOUND = new ErrorCode<>(
        "audience_not_found", 400, "Audience was not found", "audience_id");
    public static final ErrorCode<AudienceMemberRestException> INVALID_QUERY_LIMIT = new ErrorCode<>(
        "invalid_query_limit", 400, "Query limits are invalid", "audience_id", "limit", "offset");

    public AudienceMemberRestException(String uniqueId, ErrorCode<AudienceMemberRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
