package com.extole.common.jwt.decode.encrypt;

import java.text.ParseException;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.JwtParseException;
import com.extole.common.jwt.decode.SecuredUncheckedJwt;
import com.extole.common.jwt.decode.UncheckedJwtFactory;

public class EncryptedUncheckedJwtFactory implements UncheckedJwtFactory {

    @Override
    public SecuredUncheckedJwt createUncheckedJwt(String encodedJwt)
        throws UnsupportedAlgorithmException, JwtParseException {
        try {
            JWT jwt = JWTParser.parse(encodedJwt);
            return new EncryptedUncheckedJwt(encodedJwt, jwt.getHeader().toJSONObject());
        } catch (ParseException e) {
            throw new JwtParseException("Could not parse encoded jwt", e);
        }
    }
}
