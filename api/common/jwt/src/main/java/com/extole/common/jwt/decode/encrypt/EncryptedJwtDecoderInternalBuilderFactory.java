package com.extole.common.jwt.decode.encrypt;

import java.util.Map;
import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder.InternalSecuredJwtDecoderBuilder;
import com.extole.common.jwt.decode.SecuredDecoderInternalBuilderFactory;
import com.extole.common.jwt.decode.UncheckedJwt;

public class EncryptedJwtDecoderInternalBuilderFactory implements SecuredDecoderInternalBuilderFactory {

    private static final Map<Set<Algorithm>, BuilderCreator> BUILDER_CREATORS = Map.of(
        EncryptedJwtAlgorithm.SPEC_RSA_SUPPORTED_ALGORITHMS, () -> RsaEncryptedJwtDecoder.builder(),
        EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS, () -> AesEncryptedJwtDecoder.builder());

    @Override
    @SuppressWarnings("unchecked")
    public InternalSecuredJwtDecoderBuilder<UncheckedJwt> createBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return BUILDER_CREATORS.entrySet().stream()
            .filter(entry -> entry.getKey().contains(algorithm))
            .map(entry -> entry.getValue())
            .map(creator -> (InternalSecuredJwtDecoderBuilder<UncheckedJwt>) creator.createBuilder())
            .findFirst()
            .orElseThrow(() -> new UnsupportedAlgorithmException(algorithm.getName(),
                EncryptedJwtAlgorithm.SUPPORTED_ALGORITHMS));
    }

    private interface BuilderCreator {
        InternalSecuredJwtDecoderBuilder<? extends UncheckedJwt> createBuilder();
    }
}
