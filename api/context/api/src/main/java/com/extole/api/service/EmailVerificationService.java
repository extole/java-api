package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface EmailVerificationService {

    boolean isEmailValid(String email);

    EmailVerification verifyEmail(String email);
}
