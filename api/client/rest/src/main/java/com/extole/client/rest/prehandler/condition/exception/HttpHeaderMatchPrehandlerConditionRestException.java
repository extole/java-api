package com.extole.client.rest.prehandler.condition.exception;

import java.util.Map;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.common.rest.exception.ErrorCode;

public class HttpHeaderMatchPrehandlerConditionRestException
    extends PrehandlerConditionValidationRestException {

    public static final ErrorCode<HttpHeaderMatchPrehandlerConditionRestException> PREHANDLER_CONDITION_IS_EMPTY =
        new ErrorCode<>(
            "prehandler_condition_is_empty", 400,
            "Prehandler condition is empty");

    public static final ErrorCode<
        HttpHeaderMatchPrehandlerConditionRestException> PREHANDLER_CONDITION_HTTP_HEADER_NAME_INVALID =
            new ErrorCode<>(
                "prehandler_condition_http_header_name_invalid", 400,
                "Prehandler condition has an invalid http header name", "name");

    public static final ErrorCode<
        HttpHeaderMatchPrehandlerConditionRestException> PREHANDLER_CONDITION_HTTP_HEADER_VALUE_MISSING =
            new ErrorCode<>(
                "prehandler_condition_http_header_value_missing", 400,
                "Prehandler condition has a http header without a value", "name");

    public static final ErrorCode<
        HttpHeaderMatchPrehandlerConditionRestException> PREHANDLER_CONDITION_HTTP_HEADER_VALUE_INVALID =
            new ErrorCode<>(
                "prehandler_condition_http_header_value_invalid", 400,
                "Prehandler condition has a http header with an invalid value", "name", "value");

    public HttpHeaderMatchPrehandlerConditionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
