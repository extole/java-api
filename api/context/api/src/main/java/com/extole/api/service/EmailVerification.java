package com.extole.api.service;

import javax.annotation.Nullable;

public interface EmailVerification {

    interface VerifiedEmail {
        @Nullable
        String getTitle();

        String getAddress();

        String getNormalizedAddress();
    }

    boolean isEmailValid();

    @Nullable
    VerifiedEmail getVerifiedEmail();
}
