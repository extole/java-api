package com.extole.common.jwt.encode.sign;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.JwtRuntimeException;

final class InternalValidationJwsSupplier {

    private InternalValidationJwsSupplier() {
    }

    static SignedJWT get(Algorithm algorithm) {
        JWSHeader nimbusHeader = new JWSHeader(JWSAlgorithm.parse(algorithm.getSpecName()));
        try {
            return new SignedJWT(nimbusHeader, JWTClaimsSet.parse(Map.of("testKey", "testValue")));
        } catch (ParseException e) {
            throw new JwtRuntimeException("Could not create signed JWT for validation", e);
        }
    }
}
