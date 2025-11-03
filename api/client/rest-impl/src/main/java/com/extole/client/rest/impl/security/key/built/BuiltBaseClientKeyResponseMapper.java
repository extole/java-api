package com.extole.client.rest.impl.security.key.built;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import com.extole.client.rest.security.key.built.BuiltClientKeyResponse;
import com.extole.model.entity.client.security.key.ClientKey;
import com.extole.model.entity.client.security.key.built.BuiltClientKey;
import com.extole.model.service.encryption.EncryptionService;

public abstract class BuiltBaseClientKeyResponseMapper<KEY extends BuiltClientKey, RESPONSE extends BuiltClientKeyResponse>
    implements BuiltClientKeyResponseMapper<KEY, RESPONSE> {

    private static final Set<ClientKey.Algorithm> SUPPORTED_PRIVATE_KEY_ASYMMETRIC_ALGORITHMS = Set.of(
        ClientKey.Algorithm.ES256_PUBLIC,
        ClientKey.Algorithm.ES384_PUBLIC,
        ClientKey.Algorithm.ES512_PUBLIC);
    protected static final String MASK = "*****";

    private static final int LAST_VISIBLE_CHARACTERS = 3;

    private final EncryptionService<byte[]> encryptionService;

    public BuiltBaseClientKeyResponseMapper(EncryptionService<byte[]> encryptionService) {
        this.encryptionService = encryptionService;
    }

    protected final String encodeToString(byte[] key) {
        return new String(key, StandardCharsets.ISO_8859_1);
    }

    protected String maskClientKey(BuiltClientKey clientKey) {
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

    private boolean isPrivateKey(BuiltClientKey clientKey, String encodedKey) {
        if (SUPPORTED_PRIVATE_KEY_ASYMMETRIC_ALGORITHMS.contains(clientKey.getAlgorithm())) {
            try {
                Key convertedKey = convert(encodedKey.getBytes(StandardCharsets.ISO_8859_1));
                if (convertedKey instanceof PrivateKey) {
                    return true;
                }
            } catch (InvalidKeyException e) {
                return false;
            }
        }
        return false;
    }

    private Key convert(byte[] key) throws InvalidKeyException {
        try {
            return convertPublicKey(key);
        } catch (InvalidKeyException e) {
            try {
                return convertPrivateKey(key);
            } catch (InvalidKeyException ex) {
                throw new InvalidKeyException("Invalid EC key", ex);
            }
        }
    }

    private Key convertPublicKey(byte[] key) throws InvalidKeyException {
        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(key));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InvalidKeyException("Invalid EC public key", e);
        }
    }

    private Key convertPrivateKey(byte[] key) throws InvalidKeyException {
        try (PEMParser pemParser = new PEMParser(new StringReader(new String(key, StandardCharsets.ISO_8859_1)))) {
            Object object = pemParser.readObject();
            if (!(object instanceof PEMKeyPair)) {
                throw new InvalidKeyException("Invalid EC private key");
            }
            return new JcaPEMKeyConverter().getKeyPair(
                (PEMKeyPair) new PEMParser(new StringReader(
                    new String(key, StandardCharsets.ISO_8859_1))).readObject())
                .getPrivate();
        } catch (IOException e) {
            throw new InvalidKeyException("Invalid EC private key", e);
        }
    }

}
