package com.extole.common.lock;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public class LockClosureException extends Exception {
    private final Map<String, Object> parameters = Maps.newHashMap();

    public LockClosureException(Throwable cause) {
        super("Error in lock closure", cause);
    }

    public LockClosureException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Optional<Object> getParameter(String key) {
        return Optional.ofNullable(parameters.get(key));
    }
}
