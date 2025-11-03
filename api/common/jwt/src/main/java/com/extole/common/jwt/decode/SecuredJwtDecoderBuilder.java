package com.extole.common.jwt.decode;

import java.security.Key;

public interface SecuredJwtDecoderBuilder {

    SecuredJwtDecoderBuilder withKey(Key key)
        throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException;

    Jwt decode() throws InvalidSecuredJwtDecoderException, MissingDecodingKeyException;

    UncheckedJwt decodeUnchecked();
}
