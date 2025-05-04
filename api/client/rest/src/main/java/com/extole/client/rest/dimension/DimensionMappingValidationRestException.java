package com.extole.client.rest.dimension;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class DimensionMappingValidationRestException extends ExtoleRestException {

    public static final ErrorCode<DimensionMappingValidationRestException> INVALID_VALUE_FROM = new ErrorCode<>(
        "invalid_value_from", 400, "Value from can't be empty and length must be between 1 and 255", "value_from");

    public static final ErrorCode<DimensionMappingValidationRestException> INVALID_VALUE_TO = new ErrorCode<>(
        "invalid_value_to", 400, "Value to can't be empty and length must be between 1 and 255", "value_to");

    public static final ErrorCode<DimensionMappingValidationRestException> INVALID_PROGRAM_LABEL = new ErrorCode<>(
        "invalid_program_label", 400, "Program label length must be between 1 and 255", "program_label");

    public static final ErrorCode<DimensionMappingValidationRestException> INVALID_DIMENSION = new ErrorCode<>(
        "invalid_dimension", 400, "Dimension can't be empty and length must be between 1 and 255", "dimension");

    public static final ErrorCode<DimensionMappingValidationRestException> DUPLICATE_DIMENSION = new ErrorCode<>(
        "duplicate_dimension", 400, "Dimension Mapping already exists", "dimension", "program_label", "value_from");

    public DimensionMappingValidationRestException(String uniqueId,
        ErrorCode<DimensionMappingValidationRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
