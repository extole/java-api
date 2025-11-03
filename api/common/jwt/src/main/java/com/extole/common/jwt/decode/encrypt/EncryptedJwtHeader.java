package com.extole.common.jwt.decode.encrypt;

import java.util.Map;
import java.util.Optional;

import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.AbstractHeader;
import com.extole.common.jwt.decode.Header;
import com.extole.common.jwt.decode.SecuredHeader;

class EncryptedJwtHeader extends AbstractHeader implements SecuredHeader {

    private final Optional<String> keyId;

    EncryptedJwtHeader(Map<String, Object> headers) throws UnsupportedAlgorithmException {
        super(headers, EncryptedJwtAlgorithm.SUPPORTED_ALGORITHMS);
        this.keyId = getStringHeader(Header.KEY_ID);
    }

    @Override
    public Optional<String> getKeyId() {
        return keyId;
    }
}
