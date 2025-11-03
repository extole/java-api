package com.extole.common.jwt.decode.sign;

import java.util.Map;

import com.extole.common.jwt.decode.Jwt;
import com.extole.common.jwt.decode.Payload;

class SignedJwt implements Jwt {

    private final SignedJwtHeader header;
    private final Payload payload;

    SignedJwt(SignedJwtHeader header, Map<String, Object> payload) {
        this.header = header;
        this.payload = new Payload(payload);
    }

    @Override
    public SignedJwtHeader getHeader() {
        return header;
    }

    @Override
    public Payload getPayload() {
        return payload;
    }
}
