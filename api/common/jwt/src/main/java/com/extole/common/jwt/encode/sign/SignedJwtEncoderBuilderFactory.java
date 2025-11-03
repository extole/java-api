package com.extole.common.jwt.encode.sign;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.SignedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.SecuredJwtEncoder;
import com.extole.common.jwt.encode.SecuredJwtEncoderBuilderFactory;

public class SignedJwtEncoderBuilderFactory implements SecuredJwtEncoderBuilderFactory {

    private static final Map<Set<Algorithm>, JwtEncodeSecurityBuilderCreator> SIGNING_ENCODER_BUILDERS = Map.of(
        SignedJwtAlgorithm.RS_SUPPORTED_ALGORITHMS, () -> RsaSignedJwtEncoder.builder(),
        SignedJwtAlgorithm.ES_SUPPORTED_ALGORITHMS, () -> EsSignedJwtEncoder.builder(),
        SignedJwtAlgorithm.HS_SUPPORTED_ALGORITHMS, () -> HsSignedJwtEncoder.builder());

    @Override
    public SecuredJwtEncoder.SecuredJwtEncoderBuilder createBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return SIGNING_ENCODER_BUILDERS.entrySet().stream()
            .filter(entry -> entry.getKey().contains(algorithm))
            .map(entry -> entry.getValue())
            .findFirst()
            .orElseThrow(() -> new UnsupportedAlgorithmException(algorithm.getName(),
                SIGNING_ENCODER_BUILDERS.keySet().stream().flatMap(set -> set.stream()).collect(Collectors.toSet())))
            .createBuilder().withAlgorithm(algorithm);
    }

    private interface JwtEncodeSecurityBuilderCreator {
        SecuredJwtEncoder.SecuredJwtEncoderBuilder createBuilder();
    }
}
