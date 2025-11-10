package com.extole.common.rest.support.exception;

import com.extole.common.rest.exception.ExtoleRestException;

public class PolymorphicRequestTypeResolverFilterRestWrapperException extends RuntimeException {

    public PolymorphicRequestTypeResolverFilterRestWrapperException(ExtoleRestException e) {
        super(e);
    }

    @Override
    public synchronized ExtoleRestException getCause() {
        return (ExtoleRestException) super.getCause();
    }
}
