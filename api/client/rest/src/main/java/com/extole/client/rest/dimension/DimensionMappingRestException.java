package com.extole.client.rest.dimension;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class DimensionMappingRestException extends ExtoleRestException {

    public static final ErrorCode<DimensionMappingRestException> DIMENSION_MAPPING_NOT_FOUND = new ErrorCode<>(
        "dimension_mapping_not_found", 400, "Dimension mapping not found", "dimension_mapping_id");

    public DimensionMappingRestException(String uniqueId, ErrorCode<DimensionMappingRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
