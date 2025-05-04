package com.extole.api.impl.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import com.extole.api.client.security.key.ClientKey;
import com.extole.api.client.security.key.ClientKeyApiException;
import com.extole.api.service.EncoderService;

public class EncoderServiceImplTest {
    private static final EncoderService ENCODER_SERVICE = new EncoderServiceImpl();

    @Test
    public void testStringToByteArrayAndViceVersa() {
        String message = "hello";
        byte[] byteArrayFormOfTheMessage = new byte[] {104, 101, 108, 108, 111};

        assertThat(ENCODER_SERVICE.toUtf8Bytes(message))
            .isEqualTo(byteArrayFormOfTheMessage);
        assertThat(ENCODER_SERVICE.fromUtf8Bytes(byteArrayFormOfTheMessage))
            .isEqualTo(message);
    }

    @Test
    public void testEncodeHex() {
        String message = "hello";
        assertThat(ENCODER_SERVICE.encodeHex(message.getBytes()))
            .isEqualTo("68656c6c6f");
        assertThat(ENCODER_SERVICE.decodeHex(ENCODER_SERVICE.encodeHex(message.getBytes())))
            .isEqualTo(message.getBytes());
    }

    @Test
    public void testEncodeBase64() {
        String message = "hello";
        assertThat(ENCODER_SERVICE.encodeBase64(message.getBytes()))
            .isEqualTo(ENCODER_SERVICE.encodeWithBase64(message))
            .isEqualTo("aGVsbG8=");
        assertThat(ENCODER_SERVICE.decodeWithBase64("aGVsbG8="))
            .isEqualTo(new String(ENCODER_SERVICE.decodeBase64("aGVsbG8=")))
            .isEqualTo(message);
    }

    @Test
    public void testEncodeSha256() {
        assertThat(ENCODER_SERVICE.encodeHex(ENCODER_SERVICE.encodeSha256("hello".getBytes())))
            .isEqualTo(ENCODER_SERVICE.encodeWithSha256("hello"))
            .isEqualTo("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824");
    }

    @Test
    public void testEncodeWithHS256Algorithm() {
        assertThat(ENCODER_SERVICE.encodeHex(ENCODER_SERVICE.encodeHS256("key1".getBytes(), "message".getBytes())))
            .isEqualTo(
                ENCODER_SERVICE.encodeHex(ENCODER_SERVICE.encodeHS256(createClientKey("key1"), "message".getBytes())))
            .isEqualTo(ENCODER_SERVICE.encodeWithHS256Algorithm("key1", "message"))
            .isEqualTo(
                ENCODER_SERVICE.encodeWithHS256AlgorithmAndBase64Key(ENCODER_SERVICE.encodeBase64("key1".getBytes()),
                    "message"))
            .isEqualTo("e39d7763fb04d246d03f73fa5e7946c2861927a85d213975b0b6233a8b1c2208");
    }

    @Test
    public void testEncodeWithHS256AlgorithmUsingBase64Key() {
        String input = "abc";
        String expectedOutput = "30010f08cb6a611a5187fdc5faed009f5585f40d534f0d96773f8422c17da560";
        String key = "M0y6qPaxzW8=";

        String encodedMessage = ENCODER_SERVICE.encodeWithHS256AlgorithmAndBase64Key(key, input);

        assertThat(encodedMessage).isEqualTo(expectedOutput);
    }

    @Test
    public void testBothEncodeWithHS256AlgorithmUsingBytesKeyAndEncodeWithHS256AlgorithmAreSymmetric() {
        String input = "abc";
        String key = "key_1234";
        String expectedOutput = "e66f5a611b4acc8ed911753bcb2dbc7518c29dcfca81f6ad3d717605622924b5";
        UnaryOperator<String> toBase64 =
            value -> new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)));

        String encodedMessage1 = ENCODER_SERVICE.encodeWithHS256AlgorithmAndBase64Key(toBase64.apply(key), input);
        String encodedMessage2 = ENCODER_SERVICE.encodeWithHS256Algorithm(key, input);

        assertThat(encodedMessage1)
            .isEqualTo(encodedMessage2)
            .isEqualTo(expectedOutput);
    }

    private static ClientKey createClientKey(String key) {
        return new ClientKey() {

            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public String getKey() throws ClientKeyApiException {
                return key;
            }
        };
    }
}
