package com.extole.common.rest.exception;

import java.util.Map;

public interface RestException {

    String getUniqueId();

    int getHttpStatusCode();

    String getErrorCode();

    String getMessage();

    Map<String, Object> getParameters();
}
