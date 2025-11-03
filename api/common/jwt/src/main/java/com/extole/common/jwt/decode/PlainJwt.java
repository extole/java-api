package com.extole.common.jwt.decode;

import java.util.Map;

class PlainJwt implements Jwt {

    private final PlainJwtHeader header;
    private final Payload payload;

    PlainJwt(PlainJwtHeader header, Map<String, Object> payload) {
        this.header = header;
        this.payload = new Payload(payload);
    }

    @Override
    public PlainJwtHeader getHeader() {
        return header;
    }

    @Override
    public Payload getPayload() {
        return payload;
    }
}
