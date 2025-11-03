package com.extole.common.jwt.decode.encrypt;

import java.util.Map;

import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.SecuredUncheckedJwt;

class EncryptedUncheckedJwt implements SecuredUncheckedJwt {

    private final String encodedJwtString;
    private final EncryptedJwtHeader header;

    EncryptedUncheckedJwt(String encodedJwtString, Map<String, Object> headers) throws UnsupportedAlgorithmException {
        this.encodedJwtString = encodedJwtString;
        this.header = new EncryptedJwtHeader(headers);
    }

    @Override
    public String getEncodedJwtString() {
        return encodedJwtString;
    }

    @Override
    public EncryptedJwtHeader getHeader() {
        return header;
    }
}
