package com.extole.appboot.rest;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AppbootRestException extends ExtoleRestException {

    public static final ErrorCode<AppbootRestException> ACCESS_DENIED =
        new ErrorCode<>("access_denied", 403, "Access Denied", "port");

    public AppbootRestException(String uniqueId, ErrorCode<AppbootRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
