package com.extole.common.jwt.decode;

import java.security.Key;

public interface InternalSecuredJwtDecoder<UJ extends UncheckedJwt> {

    Jwt toDecodedObject(UJ uncheckedJwt) throws InvalidSecuredJwtDecoderException;

    interface InternalSecuredJwtDecoderBuilder<UJ extends UncheckedJwt> {

        InternalSecuredJwtDecoderBuilder<UJ> withKey(Key key)
            throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException;

        InternalSecuredJwtDecoder<UJ> build() throws JwtDecoderBuildException;
    }
}
