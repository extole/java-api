package com.extole.common.jwt.decode;

import java.security.Key;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.decode.encrypt.EncryptedJwtDecoderInternalBuilderFactory;
import com.extole.common.jwt.decode.sign.SignedJwtDecoderInternalBuilderFactory;

final class InternalSecuredJwtDecoderImpl implements InternalSecuredJwtDecoder<UncheckedJwt> {

    private static final Map<Set<Algorithm>, SecuredDecoderInternalBuilderFactory> INTERNAL_BUILDER_FACTORIES = Map.of(
        SignedJwtAlgorithm.SUPPORTED_ALGORITHMS, new SignedJwtDecoderInternalBuilderFactory(),
        EncryptedJwtAlgorithm.SUPPORTED_ALGORITHMS, new EncryptedJwtDecoderInternalBuilderFactory());

    private final InternalSecuredJwtDecoder<UncheckedJwt> jwtDecoder;

    private InternalSecuredJwtDecoderImpl(InternalSecuredJwtDecoder<UncheckedJwt> jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Jwt toDecodedObject(UncheckedJwt uncheckedJwt) throws InvalidSecuredJwtDecoderException {
        return jwtDecoder.toDecodedObject(uncheckedJwt);
    }

    public static Builder builder(Algorithm algorithm) throws UnsupportedAlgorithmException {
        return new Builder(getDecoderBuilder(algorithm));
    }

    static final class Builder implements InternalSecuredJwtDecoderBuilder<UncheckedJwt> {

        private final InternalSecuredJwtDecoderBuilder<UncheckedJwt> decoderBuilder;

        private Builder(InternalSecuredJwtDecoderBuilder<UncheckedJwt> decoderBuilder) {
            this.decoderBuilder = decoderBuilder;
        }

        @Override
        public Builder withKey(Key key) throws InvalidDecodingKeyTypeException, InvalidDecodingKeyAlgorithmException {
            decoderBuilder.withKey(key);
            return this;
        }

        @Override
        public InternalSecuredJwtDecoderImpl build() throws JwtDecoderBuildException {
            return new InternalSecuredJwtDecoderImpl(decoderBuilder.build());
        }
    }

    private static InternalSecuredJwtDecoderBuilder<UncheckedJwt> getDecoderBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return INTERNAL_BUILDER_FACTORIES.entrySet().stream()
            .filter(entry -> entry.getKey().contains(algorithm))
            .map(entry -> entry.getValue())
            .findFirst()
            .orElseThrow(() -> new UnsupportedAlgorithmException(algorithm.getSpecName(),
                INTERNAL_BUILDER_FACTORIES.keySet().stream().flatMap(set -> set.stream()).collect(Collectors.toSet())))
            .createBuilder(algorithm);
    }
}
