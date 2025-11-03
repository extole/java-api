package com.extole.common.jwt.decode;

public interface UncheckedJwt {

    Header getHeader();

    String getEncodedJwtString();
}
