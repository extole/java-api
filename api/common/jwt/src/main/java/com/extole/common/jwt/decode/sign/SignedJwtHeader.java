package com.extole.common.jwt.decode.sign;

import java.util.Map;
import java.util.Optional;

import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.AbstractHeader;
import com.extole.common.jwt.decode.Header;
import com.extole.common.jwt.decode.SecuredHeader;

class SignedJwtHeader extends AbstractHeader implements SecuredHeader {

    private final Optional<String> keyId;

    SignedJwtHeader(Map<String, Object> headers) throws UnsupportedAlgorithmException {
        super(headers, SignedJwtAlgorithm.SUPPORTED_ALGORITHMS);
        keyId = getStringHeader(Header.KEY_ID);
    }

    @Override
    public Optional<String> getKeyId() {
        return keyId;
    }
}
