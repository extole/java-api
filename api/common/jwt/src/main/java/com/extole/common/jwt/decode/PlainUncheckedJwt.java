package com.extole.common.jwt.decode;

import java.util.Map;

import com.extole.common.jwt.UnsupportedAlgorithmException;

class PlainUncheckedJwt implements UncheckedJwt {

    private final String encodedJwtString;
    private final PlainJwtHeader header;

    PlainUncheckedJwt(String encodedJwtString, Map<String, Object> headers)
        throws UnsupportedAlgorithmException {
        this.encodedJwtString = encodedJwtString;
        this.header = new PlainJwtHeader(headers);
    }

    public String getEncodedJwtString() {
        return encodedJwtString;
    }

    public PlainJwtHeader getHeader() {
        return header;
    }
}
