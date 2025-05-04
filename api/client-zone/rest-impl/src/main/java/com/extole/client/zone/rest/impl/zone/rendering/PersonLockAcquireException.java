package com.extole.client.zone.rest.impl.zone.rendering;

import com.extole.person.service.profile.PersonLockAcquireRuntimeException;

public class PersonLockAcquireException extends Exception {

    public PersonLockAcquireException(PersonLockAcquireRuntimeException cause) {
        super(cause);
    }

    @Override
    public synchronized PersonLockAcquireRuntimeException getCause() {
        return (PersonLockAcquireRuntimeException) super.getCause();
    }

}
