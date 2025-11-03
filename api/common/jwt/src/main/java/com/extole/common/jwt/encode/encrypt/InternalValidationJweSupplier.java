package com.extole.common.jwt.encode.encrypt;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptionMethod;
import com.extole.common.jwt.JwtRuntimeException;

final class InternalValidationJweSupplier {

    private InternalValidationJweSupplier() {
    }

    static EncryptedJWT get(Algorithm algorithm, EncryptionMethod encryptionMethod) {
        JWEHeader nimbusHeader = new JWEHeader(JWEAlgorithm.parse(algorithm.getSpecName()),
            com.nimbusds.jose.EncryptionMethod.parse(encryptionMethod.getSpecName()));
        try {
            return new EncryptedJWT(nimbusHeader, JWTClaimsSet.parse(Map.of("testKey", "testValue")));
        } catch (ParseException e) {
            throw new JwtRuntimeException("Could not create encrypted JWT for validation", e);
        }
    }
}
