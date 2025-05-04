package com.extole.consumer.rest.debug;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CreativeMetricRestException extends ExtoleRestException {

    public static final ErrorCode<CreativeMetricRestException> INVALID_METRIC_TYPE = new ErrorCode<>(
        "invalid_metric_type", 400, "Metric type unsupported", "supported_metric_types", "metric_type");

    public static final ErrorCode<CreativeMetricRestException> INVALID_KEY = new ErrorCode<>(
        "invalid_key", 400, "Key must only contain alphanumerics and underscores", "key");

    public static final ErrorCode<CreativeMetricRestException> MISSING_REQUIRED_FIELD = new ErrorCode<>(
        "missing_required_field", 400, "Required field is missing", "name");

    public static final ErrorCode<CreativeMetricRestException> KEY_IS_ALREADY_USED = new ErrorCode<>(
        "key_is_already_used", 400, "Key is already used for a different type of metric", "metric_type");

    public CreativeMetricRestException(String uniqueId, ErrorCode<CreativeMetricRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
