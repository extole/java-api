package com.extole.client.rest.security.key.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

public class JwtClientKeyVerifyJwtResponse {

    private static final String RESULT = "result";
    private static final String DESCRIPTION = "description";
    private final Result result;

    public JwtClientKeyVerifyJwtResponse(@JsonProperty(RESULT) Result result) {
        this.result = result;
    }

    @JsonProperty(RESULT)
    public Result getResult() {
        return result;
    }

    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return result.getDescription();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @Schema
    public enum Result {
        MALFORMED("The token could not be verified, make sure it is in compliance with rfc7519 specification."),
        MALFORMED_EXP_CLAIM(
            "The token could not be verified, make sure the exp claim is in compliance with rfc7519 specification."),
        MALFORMED_NBF_CLAIM(
            "The token could not be verified, make sure the nbf claim is in compliance with rfc7519 specification."),
        EXPIRED("The token could not be verified, exp claim should be in the future."),
        PREMATURE_USE("The token could not be verified, nbf claim should be in the past."),
        KEY_ID_MISSING("The token could not be verified, key id (kid) header should be specified."),
        KEY_ID_MISMATCH(
            "The token could not be verified, key id (kid) header does not correspond with key partner id."),
        ALGORITHM_MISSING("The token could not be verified, algorithm (alg) header should be specified."),
        ALGORITHM_MISMATCH(
            "The token could not be verified, algorithm (alg) header does not correspond with key algorithm."),
        SIGNATURE_MISMATCH("The token could not be verified," +
            " the signature computed using specified key does not match with the provided one."),
        DECRYPTION_FAILURE("The token could not be decrypted," +
            " the ciphertext is computed using another key and/or was tampered."),
        UNSUPPORTED("The token could not be verified," +
            " the token is unsecured and/or has unsupported format/encoding."),
        MATCHED("The token is valid.");

        private final String description;

        Result(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

}
