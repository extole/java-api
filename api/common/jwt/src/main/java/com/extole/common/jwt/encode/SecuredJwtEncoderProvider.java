package com.extole.common.jwt.encode;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.encrypt.EncryptedJwtEncoder;
import com.extole.common.jwt.encode.encrypt.EncryptedJwtEncoderBuilderFactory;
import com.extole.common.jwt.encode.sign.SignedJwtEncoderBuilderFactory;

public final class SecuredJwtEncoderProvider {

    private static final EncryptedJwtEncoderBuilderFactory ENCRYPTED_JWT_ENCODER_BUILDER_FACTORY =
        new EncryptedJwtEncoderBuilderFactory();
    private static final SignedJwtEncoderBuilderFactory SIGNED_JWT_ENCODER_BUILDER_FACTORY =
        new SignedJwtEncoderBuilderFactory();

    private SecuredJwtEncoderProvider() {
    }

    public static SecuredJwtEncoder.SecuredJwtEncoderBuilder createSigningEncoder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return SIGNED_JWT_ENCODER_BUILDER_FACTORY.createBuilder(algorithm);
    }

    public static EncryptedJwtEncoder.EncryptedJwtEncoderBuilder createEncryptingEncoder(Algorithm algorithm)
        throws UnsupportedAlgorithmException {
        return ENCRYPTED_JWT_ENCODER_BUILDER_FACTORY.createBuilder(algorithm);
    }
}
