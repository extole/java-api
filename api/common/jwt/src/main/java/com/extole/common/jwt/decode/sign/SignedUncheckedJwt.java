package com.extole.common.jwt.decode.sign;

import java.util.Map;

import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.SecuredUncheckedJwt;

class SignedUncheckedJwt implements SecuredUncheckedJwt {

    private final String encodedJwtString;
    private final SignedJwtHeader header;

    SignedUncheckedJwt(String encodedJwtString, Map<String, Object> headers)
        throws UnsupportedAlgorithmException {
        this.encodedJwtString = encodedJwtString;
        this.header = new SignedJwtHeader(headers);
    }

    public String getEncodedJwtString() {
        return encodedJwtString;
    }

    public SignedJwtHeader getHeader() {
        return header;
    }
}
