package com.extole.common.jwt.encode;

import java.security.Key;
import java.util.Map;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;

public interface SecuredJwtEncoder {

    String toEncodedString(Map<String, Object> headers, Map<String, Object> claims);

    interface SecuredJwtEncoderBuilder {
        SecuredJwtEncoderBuilder withAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException;

        SecuredJwtEncoderBuilder withKey(Key key) throws InvalidEncodingKeyTypeException;

        SecuredJwtEncoder build() throws JwtEncoderBuildException;
    }
}
