package com.extole.consumer.rest.me;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class MeDataRestException extends ExtoleRestException {
    public static final ErrorCode<MeDataRestException> INVALID_VALUE =
        new ErrorCode<>("invalid_value", 403, "One or more values supplied were invalid");

    public static final ErrorCode<MeDataRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 403, "One or more names supplied were invalid", "name");

    public static final ErrorCode<MeDataRestException> READ_ONLY_NAME =
        new ErrorCode<>("read_only_name", 403, "One or more names supplied are read only", "name");

    public static final ErrorCode<MeDataRestException> CLIENT_PARAMS_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("client_params_value_length_out_of_range", 403, "The value supplied is too long");

    public static final ErrorCode<MeDataRestException> CLIENT_PARAMS_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("client_params_name_length_out_of_range", 403, "The name supplied is too long", "name");

    public static final ErrorCode<MeDataRestException> CLIENT_PARAM_NOT_FOUND =
        new ErrorCode<>("client_param_not_found", 403, "Parameter not found", "name");

    public MeDataRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
