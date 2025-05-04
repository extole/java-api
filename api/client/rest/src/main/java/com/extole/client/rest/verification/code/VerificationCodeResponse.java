package com.extole.client.rest.verification.code;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class VerificationCodeResponse {
    private static final String VERIFICATION_CODE = "verification_code";
    private static final String IS_VALID = "is_valid";

    private final String verificationCode;
    private final boolean isValid;

    public VerificationCodeResponse(
        @JsonProperty(VERIFICATION_CODE) String verificationCode,
        @JsonProperty(IS_VALID) boolean isValid) {
        this.verificationCode = verificationCode;
        this.isValid = isValid;
    }

    @JsonProperty(VERIFICATION_CODE)
    public String getVerificationCode() {
        return verificationCode;
    }

    @JsonProperty(IS_VALID)
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
