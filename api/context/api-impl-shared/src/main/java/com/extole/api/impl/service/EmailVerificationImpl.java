package com.extole.api.impl.service;

import javax.annotation.Nullable;

import com.extole.api.service.EmailVerification;
import com.extole.common.lang.ToString;

public final class EmailVerificationImpl implements EmailVerification {
    private final VerifiedEmail verifiedEmail;

    private EmailVerificationImpl(@Nullable VerifiedEmail verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public static EmailVerification valid(VerifiedEmail verifiedEmail) {
        return new EmailVerificationImpl(verifiedEmail);
    }

    public static EmailVerification invalid() {
        return new EmailVerificationImpl(null);
    }

    @Override
    public boolean isEmailValid() {
        return verifiedEmail != null;
    }

    @Nullable
    @Override
    public VerifiedEmail getVerifiedEmail() {
        return verifiedEmail;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
