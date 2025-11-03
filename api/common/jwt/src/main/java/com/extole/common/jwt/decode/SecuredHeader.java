package com.extole.common.jwt.decode;

import java.util.Optional;

public interface SecuredHeader extends Header {

    Optional<String> getKeyId();
}
