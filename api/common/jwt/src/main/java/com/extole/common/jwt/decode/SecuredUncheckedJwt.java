package com.extole.common.jwt.decode;

public interface SecuredUncheckedJwt extends UncheckedJwt {

    @Override
    SecuredHeader getHeader();
}
