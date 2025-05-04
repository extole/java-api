package com.extole.client.rest.security.key;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildClientKeyRestException extends ExtoleRestException {

    public static final ErrorCode<BuildClientKeyRestException> CLIENT_KEY_MISSING_NAME = new ErrorCode<>(
        "client_key_missing_name", 400, "Key required name is not specified");

    public static final ErrorCode<BuildClientKeyRestException> CLIENT_KEY_INVALID_NAME = new ErrorCode<>(
        "client_key_invalid_name", 400, "Allowed name length is 255 containing ASCII characters", "name");

    public static final ErrorCode<BuildClientKeyRestException> CLIENT_KEY_INVALID_DESCRIPTION =
        new ErrorCode<>(
            "client_key_invalid_description", 400, "Allowed description length is 1024 containing ASCII characters",
            "description");

    public static final ErrorCode<BuildClientKeyRestException> CLIENT_KEY_BUILD_FAILED =
        new ErrorCode<>("client_key_build_failed", 400, "Client key build failed",
            "client_key_id", "evaluatable_name", "evaluatable");

    public BuildClientKeyRestException(String uniqueId, ErrorCode<BuildClientKeyRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
