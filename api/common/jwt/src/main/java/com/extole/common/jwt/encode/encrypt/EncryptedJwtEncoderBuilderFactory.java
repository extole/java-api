package com.extole.common.jwt.encode.encrypt;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptedJwtAlgorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.SecuredJwtEncoderBuilderFactory;

public class EncryptedJwtEncoderBuilderFactory implements SecuredJwtEncoderBuilderFactory {

    private static final Map<Set<Algorithm>, JwtEncryptingEncodeSecurityBuilderCreator> ENCRYPTING_ENCODER_BUILDERS =
        Map.of(EncryptedJwtAlgorithm.RSA_SUPPORTED_ALGORITHMS, () -> RsaEncryptedJwtEncoder.builder(),
            EncryptedJwtAlgorithm.AES_SUPPORTED_ALGORITHMS, () -> AesEncryptedJwtEncoder.builder());

    @Override
    public EncryptedJwtEncoder.EncryptedJwtEncoderBuilder createBuilder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return ENCRYPTING_ENCODER_BUILDERS.entrySet().stream()
            .filter(entry -> entry.getKey().contains(algorithm))
            .map(entry -> entry.getValue())
            .findFirst()
            .orElseThrow(() -> new UnsupportedAlgorithmException(algorithm.getName(),
                ENCRYPTING_ENCODER_BUILDERS.keySet().stream().flatMap(set -> set.stream()).collect(Collectors.toSet())))
            .createBuilder().withAlgorithm(algorithm);
    }

    private interface JwtEncryptingEncodeSecurityBuilderCreator {
        EncryptedJwtEncoder.EncryptedJwtEncoderBuilder createBuilder();
    }
}
