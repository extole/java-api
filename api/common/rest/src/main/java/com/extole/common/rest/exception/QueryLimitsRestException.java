package com.extole.common.rest.exception;

import java.util.Map;

public class QueryLimitsRestException extends ExtoleRestException {

    public static final ErrorCode<QueryLimitsRestException> INVALID_LIMIT = new ErrorCode<>(
        "invalid_limit", 400, "Limit should be a non negative integer", "limit");

    public static final ErrorCode<QueryLimitsRestException> INVALID_OFFSET = new ErrorCode<>(
        "invalid_offset", 400, "Offset should be a non negative integer", "offset");

    public static final ErrorCode<QueryLimitsRestException> MAX_FETCH_SIZE_1000 = new ErrorCode<>(
        "max_fetch_size_1000", 400, "Maximum allowed difference between limit and offset should be 1000", "limit",
        "offset");

    public QueryLimitsRestException(String uniqueId, ErrorCode<QueryLimitsRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
