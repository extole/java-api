package com.extole.common.jwt.decode.sign;

import java.util.Map;
import java.util.Set;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder;
import com.extole.common.jwt.decode.InternalSecuredJwtDecoder.InternalSecuredJwtDecoderBuilder;
import com.extole.common.jwt.decode.SecuredDecoderInternalBuilderFactory;
import com.extole.common.jwt.decode.UncheckedJwt;

public class SignedJwtDecoderInternalBuilderFactory implements SecuredDecoderInternalBuilderFactory {

    private static final Map<Set<Algorithm>, BuilderCreator> BUILDER_CREATORS = Map.of(
        SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS, () -> HsSignedJwtDecoder.builder(),
        SignedJwtAlgorithm.ES_SUPPORTED_ALGORITHMS, () -> EsSignedJwtDecoder.builder(),
        SignedJwtAlgorithm.SPEC_RS_SUPPORTED_ALGORITHMS, () -> RsaSignedJwtDecoder.builder());

    @Override
    @SuppressWarnings("unchecked")
    public InternalSecuredJwtDecoderBuilder<UncheckedJwt> createBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return BUILDER_CREATORS.entrySet().stream()
            .filter(entry -> entry.getKey().contains(algorithm))
            .map(entry -> entry.getValue())
            .map(creator -> (InternalSecuredJwtDecoderBuilder<UncheckedJwt>) creator.createBuilder())
            .findFirst()
            .orElseThrow(
                () -> new UnsupportedAlgorithmException(algorithm.getName(), SignedJwtAlgorithm.SUPPORTED_ALGORITHMS));
    }

    private interface BuilderCreator {
        InternalSecuredJwtDecoder.InternalSecuredJwtDecoderBuilder<? extends UncheckedJwt> createBuilder();
    }
}
