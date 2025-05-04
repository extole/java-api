package com.extole.reporting.rest.query;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class QueryRestException extends ExtoleRestException {

    public static final ErrorCode<QueryRestException> QUERY_UNKNOWN =
        new ErrorCode<>("query_unknown", 403, "Unknown query", "name");

    public static final ErrorCode<QueryRestException> QUERY_MISSING_PARAMETERS =
        new ErrorCode<>("query_missing_parameters", 403, "Missing query parameters", "parameters");

    public static final ErrorCode<QueryRestException> QUERY_INVALID_PARAMETERS =
        new ErrorCode<>("query_invalid_parameters", 400, "Report parameter(s) of invalid format", "parameters");

    public static final ErrorCode<QueryRestException> QUERY_EXECUTION_ERROR =
        new ErrorCode<>("query_execution_error", 500, "Query execution error");

    public QueryRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
