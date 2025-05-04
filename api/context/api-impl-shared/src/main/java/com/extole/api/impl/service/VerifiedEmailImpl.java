package com.extole.api.impl.service;

import com.extole.api.service.EmailVerification.VerifiedEmail;
import com.extole.common.email.Email;
import com.extole.common.lang.ToString;

public class VerifiedEmailImpl implements VerifiedEmail {
    private final String title;
    private final String address;
    private final String normalizedAddress;

    public VerifiedEmailImpl(Email email) {
        this.title = email.getTitle().orElse(null);
        this.address = email.getAddress();
        this.normalizedAddress = email.getNormalizedAddress();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getNormalizedAddress() {
        return normalizedAddress;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
