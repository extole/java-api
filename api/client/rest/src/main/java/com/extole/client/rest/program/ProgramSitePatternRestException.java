package com.extole.client.rest.program;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ProgramSitePatternRestException extends ExtoleRestException {

    public static final ErrorCode<ProgramSitePatternRestException> INVALID_SITE_PATTERN =
        new ErrorCode<>("program_invalid_site_pattern", 400, "Unable to access site pattern");

    public static final ErrorCode<ProgramSitePatternRestException> INVALID_SITE_PATTERN_SYNTAX =
        new ErrorCode<>("invalid_site_pattern_syntax", 400, "The site pattern has syntax errors", "type",
            "site_pattern");

    public static final ErrorCode<ProgramSitePatternRestException> INVALID_SITE_PATTERN_DOMAIN =
        new ErrorCode<>("invalid_site_pattern_domain", 400, "The site pattern must be a valid domain", "type",
            "site_pattern");

    public ProgramSitePatternRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
