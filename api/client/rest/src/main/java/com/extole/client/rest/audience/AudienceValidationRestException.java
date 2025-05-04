package com.extole.client.rest.audience;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class AudienceValidationRestException extends AudienceRestException {

    public static final ErrorCode<AudienceValidationRestException> INVALID_AUDIENCE_TAG =
        new ErrorCode<>("invalid_audience_tag", 400, "Invalid audience tag", "tag", "tag_max_length");

    public static final ErrorCode<AudienceValidationRestException> INVALID_COMPONENT_REFERENCE = new ErrorCode<>(
        "invalid_component_reference", 400, "Unknown referenced campaign component", "identifier_type", "identifier");

    public AudienceValidationRestException(String uniqueId, ErrorCode<AudienceRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
