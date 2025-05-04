package com.extole.client.rest.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SourceMappingRestException extends ExtoleRestException {

    public static final ErrorCode<SourceMappingRestException> SOURCE_MAPPING_NOT_FOUND = new ErrorCode<>(
        "source_mapping_not_found", 400, "Source mapping not found", "identity_id");

    public SourceMappingRestException(String uniqueId, ErrorCode<SourceMappingRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
