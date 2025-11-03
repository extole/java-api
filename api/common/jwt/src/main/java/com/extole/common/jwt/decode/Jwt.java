package com.extole.common.jwt.decode;

public interface Jwt {

    Header getHeader();

    Payload getPayload();

}
