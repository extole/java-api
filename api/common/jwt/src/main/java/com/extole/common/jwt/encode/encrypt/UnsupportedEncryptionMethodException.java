package com.extole.common.jwt.encode.encrypt;

import java.util.Set;

import com.extole.common.jwt.EncryptionMethod;
import com.extole.common.jwt.JwtException;

public class UnsupportedEncryptionMethodException extends JwtException {

    private static final String MESSAGE_PATTERN =
        "Cannot encrypt JWT with encryption method: %s. Supported encryption methods: %s";

    public UnsupportedEncryptionMethodException(EncryptionMethod encryptionMethod,
        Set<EncryptionMethod> supportedAlgorithms) {
        super(String.format(MESSAGE_PATTERN, encryptionMethod.getSpecName(), supportedAlgorithms));
    }
}
