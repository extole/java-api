package com.extole.client.rest.audience;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildAudienceRestException extends ExtoleRestException {

    public static final ErrorCode<BuildAudienceRestException> MISSING_AUDIENCE_NAME = new ErrorCode<>(
        "missing_audience_name", 400, "Audience required name is not specified");

    public static final ErrorCode<BuildAudienceRestException> INVALID_AUDIENCE_NAME = new ErrorCode<>(
        "invalid_audience_name", 400, "Invalid audience name", "name");

    public static final ErrorCode<BuildAudienceRestException> AUDIENCE_BUILD_FAILED =
        new ErrorCode<>("audience_build_failed", 400, "Audience build failed",
            "audience_id", "evaluatable_name", "evaluatable");

    public BuildAudienceRestException(String uniqueId, ErrorCode<BuildAudienceRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
