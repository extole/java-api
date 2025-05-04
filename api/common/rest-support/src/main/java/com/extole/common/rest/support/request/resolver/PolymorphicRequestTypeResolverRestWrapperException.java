package com.extole.common.rest.support.request.resolver;

import com.extole.common.rest.exception.ExtoleRestException;

public class PolymorphicRequestTypeResolverRestWrapperException extends PolymorphicRequestTypeResolverException {

    public PolymorphicRequestTypeResolverRestWrapperException(String message, ExtoleRestException cause) {
        super(message, cause);
    }

    @Override
    public synchronized ExtoleRestException getCause() {
        return (ExtoleRestException) super.getCause();
    }
}
