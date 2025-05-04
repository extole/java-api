package com.extole.client.rest.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SourceMappingValidationRestException extends ExtoleRestException {

    public static final ErrorCode<SourceMappingValidationRestException> INVALID_SOURCE_FROM = new ErrorCode<>(
        "invalid_source_from", 400, "Source from can't be empty and length must be between 1 and 255", "source_from");

    public static final ErrorCode<SourceMappingValidationRestException> INVALID_SOURCE_TO = new ErrorCode<>(
        "invalid_source_to", 400, "Source to can't be empty and length must be between 1 and 255", "source_to");

    public static final ErrorCode<SourceMappingValidationRestException> INVALID_PROGRAM_LABEL = new ErrorCode<>(
        "invalid_program_label", 400, "Program label can't be empty and length must be between 1 and 255",
        "program_label");

    public static final ErrorCode<SourceMappingValidationRestException> DUPLICATED_MAPPING = new ErrorCode<>(
        "duplicated_mapping", 400, "Source dimension mapping already exists", "program_label", "source_from");

    public SourceMappingValidationRestException(String uniqueId, ErrorCode<SourceMappingValidationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
