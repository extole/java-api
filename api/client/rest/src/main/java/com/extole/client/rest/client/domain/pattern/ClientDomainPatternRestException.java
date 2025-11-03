package com.extole.client.rest.client.domain.pattern;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientDomainPatternRestException extends ExtoleRestException {

    public static final ErrorCode<ClientDomainPatternRestException> CLIENT_DOMAIN_PATTERN_NOT_FOUND = new ErrorCode<>(
        "client_domain_pattern_not_found", 400, "Client domain pattern was not found", "id");

    public ClientDomainPatternRestException(String uniqueId, ErrorCode<ClientDomainPatternRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
