package com.extole.client.rest.prehandler.action.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class MapDataAttributesPrehandlerActionRestException extends PrehandlerActionValidationRestException {

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> MISSING_ATTRIBUTE = new ErrorCode<>(
        "missing_attribute", 400, "Source and target attribute must be specified");

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> REDUNDANT_ATTRIBUTES =
        new ErrorCode<>("redundant_attribute_mapping", 400, "Source and target attributes must differ", "name");

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> DUPLICATED_ATTRIBUTES =
        new ErrorCode<>("duplicated_attributes", 400, "All attributes must be distinct", "name");

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> DUPLICATED_SOURCE_ATTRIBUTES =
        new ErrorCode<>("duplicated_source_attributes", 400, "All source attributes must be distinct", "name");

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> MISSING_DATA_ATTRIBUTE_MAPPINGS =
        new ErrorCode<>("missing_attribute_mappings", 400, "Missing attribute mappings");

    public static final ErrorCode<MapDataAttributesPrehandlerActionRestException> DEFAULT_VALUE_INVALID =
        new ErrorCode<>("default_value_invalid", 400, "Default value is invalid", "name");

    public MapDataAttributesPrehandlerActionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
