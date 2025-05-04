package com.extole.common.rest.support.filter;

@FunctionalInterface
public interface RequestOriginValidator {
    boolean isValid(String requestDomain, String origin);
}
