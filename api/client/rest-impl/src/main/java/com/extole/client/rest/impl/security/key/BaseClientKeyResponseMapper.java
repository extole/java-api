package com.extole.client.rest.impl.security.key;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;

import com.extole.client.rest.security.key.ClientKeyResponse;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.service.encryption.EncryptionService;
import com.extole.model.service.jwt.JwtAlgorithm;

public abstract class BaseClientKeyResponseMapper<KEY extends ClientKey, RESPONSE extends ClientKeyResponse>
    implements ClientKeyResponseMapper<KEY, RESPONSE> {

    protected static final String MASK = "*****";

    private static final int LAST_VISIBLE_CHARACTERS = 3;

    private final EncryptionService<byte[]> encryptionService;

    public BaseClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        this.encryptionService = encryptionService;
    }

    protected final String encodeToString(byte[] key) {
        return new String(key, StandardCharsets.ISO_8859_1);
    }

    protected String maskClientKey(ClientKey clientKey) {
        String encodedKey = encodeToString(encryptionService.decrypt(clientKey.getEncryptedKey()));
        boolean returnMaskedKey = clientKey.getAlgorithm().isSymmetric() || isPrivateKey(clientKey, encodedKey);

        return returnMaskedKey
            ? mask(encodedKey)
            : encodedKey;
    }

    protected String mask(String key) {
        if (key.length() < LAST_VISIBLE_CHARACTERS * 2) {
            return MASK;
        }
        return key.charAt(0) + MASK + key.substring(key.length() - LAST_VISIBLE_CHARACTERS);
    }

    private boolean isPrivateKey(ClientKey clientKey, String encodedKey) {
        try {
            Key convertedKey = convert(clientKey.getAlgorithm(), encodedKey.getBytes(StandardCharsets.ISO_8859_1));
            return convertedKey instanceof PrivateKey;
        } catch (InvalidKeyException e) {
            return false;
        }
    }

    private Key convert(ClientKey.Algorithm algorithm, byte[] key) throws InvalidKeyException {
        return JwtAlgorithm.valueOf(algorithm.name()).convert(key);
    }
}
