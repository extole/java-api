package com.extole.client.rest.targeting.settings;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TargetingSettingsRestException extends ExtoleRestException {

    public static final ErrorCode<TargetingSettingsRestException> INVALID_TARGETING_VERSION =
        new ErrorCode<>("invalid_targeting_version", 400, "Invalid targeting version");

    public static final ErrorCode<TargetingSettingsRestException> DRY_RUN_VERSION_MATCHES_EXECUTION_VERSION =
        new ErrorCode<>("dry_run_version_matches_execution_version", 400,
            "Targeting dry run version should not match execution version", "dry_run_version", "version");

    public TargetingSettingsRestException(String uniqueId,
        ErrorCode<TargetingSettingsRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
