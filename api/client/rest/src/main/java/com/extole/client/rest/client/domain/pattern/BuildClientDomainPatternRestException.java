package com.extole.client.rest.client.domain.pattern;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildClientDomainPatternRestException extends ExtoleRestException {

    public static final ErrorCode<BuildClientDomainPatternRestException> MISSING_CLIENT_DOMAIN_PATTERN =
        new ErrorCode<>("missing_client_domain_pattern", 400, "Client domain pattern is required",
            "client_domain_pattern_id", "evaluatable_name", "evaluatable");

    public static final ErrorCode<BuildClientDomainPatternRestException> INVALID_CLIENT_DOMAIN_PATTERN_SYNTAX =
        new ErrorCode<>("invalid_client_domain_pattern_syntax", 400, "The client domain pattern has syntax errors",
            "type", "pattern");

    public static final ErrorCode<BuildClientDomainPatternRestException> INVALID_CLIENT_DOMAIN_PATTERN_DOMAIN =
        new ErrorCode<>("invalid_client_domain_pattern_domain", 400, "The client domain pattern must be a valid domain",
            "type", "pattern");

    public static final ErrorCode<BuildClientDomainPatternRestException> CLIENT_DOMAIN_PATTERN_BUILD_FAILED =
        new ErrorCode<>("client_domain_pattern_build_failed", 400, "Client domain pattern build failed",
            "client_domain_pattern_id", "evaluatable_name", "evaluatable");

    public BuildClientDomainPatternRestException(String uniqueId, ErrorCode<BuildClientDomainPatternRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
