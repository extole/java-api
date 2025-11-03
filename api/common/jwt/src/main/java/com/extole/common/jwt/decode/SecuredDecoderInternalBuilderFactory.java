package com.extole.common.jwt.decode;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;

public interface SecuredDecoderInternalBuilderFactory {

    InternalSecuredJwtDecoder.InternalSecuredJwtDecoderBuilder<UncheckedJwt> createBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException;
}
