package com.extole.common.jwt.decode;

import java.text.ParseException;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import com.extole.common.jwt.UnsupportedAlgorithmException;

public class PlainUncheckedJwtFactory implements UncheckedJwtFactory {

    @Override
    public UncheckedJwt createUncheckedJwt(String encodedJwt) throws JwtParseException, UnsupportedAlgorithmException {
        try {
            JWT jwt = JWTParser.parse(encodedJwt);
            return new PlainUncheckedJwt(encodedJwt, jwt.getHeader().toJSONObject());
        } catch (ParseException e) {
            throw new JwtParseException("Could not parse encoded jwt", e);
        }
    }
}
