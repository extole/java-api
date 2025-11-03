package com.extole.common.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class SecureEmailServiceImpl implements SecureEmailService {
    public static final int MAX_SIZE = 16;
    public static final int LIMIT = 8;
    public static final int NEW_LENGTH_TO_COPY = 24;
    private final SecretKey secretKey;
    private final IvParameterSpec ivParameterSpec;

    @Autowired
    SecureEmailServiceImpl(@Value("${secure.email.crypto.key:SecureEmailServiceEncryptionKey}") String keyVector,
        @Value("${secure.email.crypto.iv:20180626}") String initializationVector) {
        try {
            MessageDigest message = MessageDigest.getInstance("md5");
            byte[] digestOfPassword = message.digest(Base64.getEncoder().encode(keyVector.getBytes(UTF_8)));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, NEW_LENGTH_TO_COPY);
            for (int j = 0, k = MAX_SIZE; j < LIMIT;) {
                keyBytes[k++] = keyBytes[j++];
            }

            KeySpec keySpec = new DESedeKeySpec(keyBytes);
            secretKey = SecretKeyFactory.getInstance("DESede").generateSecret(keySpec);
            ivParameterSpec = new IvParameterSpec(initializationVector.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String secureEmail(String email) throws EncrypterException {
        try {
            Cipher ecipher = Cipher.getInstance("DESede/CBC/PKCS5Padding", "SunJCE");
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] utf8Value = email.getBytes(UTF_8);
            byte[] encoded = ecipher.doFinal(utf8Value);
            return new String(Base64.getUrlEncoder().encode(encoded));
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
            | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncrypterException(e);
        }
    }

    @Override
    public String readSecureEmail(String secureEmail) throws EncrypterException {
        try {
            Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding", "SunJCE");
            decipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decodedValue = Base64.getUrlDecoder().decode(secureEmail.getBytes());
            byte[] utf8DecodedValue = decipher.doFinal(decodedValue);
            return new String(utf8DecodedValue, UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException
            | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
            | IllegalArgumentException e) {
            throw new EncrypterException(e);
        }
    }

}
