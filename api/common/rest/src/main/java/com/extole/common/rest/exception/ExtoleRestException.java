package com.extole.common.rest.exception;

import java.util.Map;

public abstract class ExtoleRestException extends Exception implements RestException {
    protected String uniqueId;
    protected ErrorCode<?> errorCode;
    protected Map<String, Object> parameters;

    public ExtoleRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.uniqueId = uniqueId;
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public int getHttpStatusCode() {
        return errorCode.getHttpCode();
    }

    @Override
    public String getErrorCode() {
        return errorCode.getName();
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

}
