package com.extole.common.jwt.decode;

import com.extole.common.jwt.UnsupportedAlgorithmException;

public interface UncheckedJwtFactory {

    UncheckedJwt createUncheckedJwt(String encodedJwt) throws JwtParseException, UnsupportedAlgorithmException;
}
