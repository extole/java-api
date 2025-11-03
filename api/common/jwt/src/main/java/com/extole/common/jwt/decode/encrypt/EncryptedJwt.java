package com.extole.common.jwt.decode.encrypt;

import java.util.Map;

import com.extole.common.jwt.decode.Jwt;
import com.extole.common.jwt.decode.Payload;

class EncryptedJwt implements Jwt {

    private final EncryptedJwtHeader header;
    private final Payload payload;

    EncryptedJwt(EncryptedJwtHeader header, Map<String, Object> payload) {
        this.header = header;
        this.payload = new Payload(payload);
    }

    @Override
    public EncryptedJwtHeader getHeader() {
        return header;
    }

    @Override
    public Payload getPayload() {
        return payload;
    }
}
