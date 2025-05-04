package com.extole.api.client.security.key;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.extole.api.ApiException;
import com.extole.common.lang.exception.NotifiableException;

@NotifiableException
public class ClientKeyApiException extends Exception implements ApiException {

    private final Map<String, String> parameters;
    private final ErrorCode errorCode;

    public ClientKeyApiException(ErrorCode errorCode, Exception cause) {
        super(cause);
        this.errorCode = errorCode;
        this.parameters = Collections.emptyMap();
    }

    public ClientKeyApiException(ErrorCode errorCode, Exception cause, Map<String, String> parameters) {
        super(cause);
        this.errorCode = errorCode;
        this.parameters = ImmutableMap.copyOf(parameters);
    }

    public enum ErrorCode {
        ACCESS_TOKEN_UNAVAILABLE
    }

    @Override
    public String getCode() {
        return errorCode.name();
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

}
