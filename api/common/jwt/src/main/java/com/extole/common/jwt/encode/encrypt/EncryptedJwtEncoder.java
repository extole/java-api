package com.extole.common.jwt.encode.encrypt;

import java.security.Key;

import com.extole.common.jwt.Algorithm;
import com.extole.common.jwt.EncryptionMethod;
import com.extole.common.jwt.UnsupportedAlgorithmException;
import com.extole.common.jwt.encode.InvalidEncodingKeyTypeException;
import com.extole.common.jwt.encode.SecuredJwtEncoder;

public interface EncryptedJwtEncoder extends SecuredJwtEncoder {

    interface EncryptedJwtEncoderBuilder extends SecuredJwtEncoderBuilder {

        @Override
        EncryptedJwtEncoderBuilder withAlgorithm(Algorithm algorithm) throws UnsupportedAlgorithmException;

        @Override
        EncryptedJwtEncoderBuilder withKey(Key key) throws InvalidEncodingKeyTypeException;

        EncryptedJwtEncoderBuilder withEncryptionMethod(EncryptionMethod encryptionMethod)
            throws UnsupportedEncryptionMethodException;
    }
}
