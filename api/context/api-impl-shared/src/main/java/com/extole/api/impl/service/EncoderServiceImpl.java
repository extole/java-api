package com.extole.api.impl.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import com.extole.api.client.security.key.ClientKey;
import com.extole.api.client.security.key.ClientKeyApiException;
import com.extole.api.service.EncoderService;
import com.extole.encoder.Encoder;

public class EncoderServiceImpl implements EncoderService {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[] {};
    private static final String HMAC_SHA_256 = "HmacSHA256";

    @Override
    public String safeHtml(String input) {
        return Encoder.getInstance().safeHtml(input);
    }

    @Override
    public String safeHtmlContent(String input) {
        return Encoder.getInstance().safeHtmlContent(input);
    }

    @Override
    public String safeHtmlAttribute(String input) {
        return Encoder.getInstance().safeHtmlAttribute(input);
    }

    @Override
    public String safeCssString(String input) {
        return Encoder.getInstance().safeCssString(input);
    }

    @Override
    public String safeCssUrl(String input) {
        return Encoder.getInstance().safeCssUrl(input);
    }

    @Override
    public String safeJs(String input) {
        return Encoder.getInstance().safeJavaScript(input);
    }

    @Override
    public String safeJsBlock(String input) {
        return Encoder.getInstance().safeJavaScriptBlock(input);
    }

    @Override
    public String safeJsAttribute(String input) {
        return Encoder.getInstance().safeJavaScriptAttribute(input);
    }

    @Override
    public String safeUriComponent(String input) {
        return Encoder.getInstance().safeUriComponent(input);
    }

    @Override
    public String safeHtmlUnquotedAttribute(String input) {
        return Encoder.getInstance().safeHtmlUnquotedAttribute(input);
    }

    @Override
    public String encodeWithHS256Algorithm(String key, String message) {
        return encodeWithHS256Algorithm(key.getBytes(StandardCharsets.UTF_8), message);
    }

    @Override
    public String encodeWithHS256AlgorithmAndBase64Key(String key, String message) {
        return encodeWithHS256Algorithm(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)), message);
    }

    @Override
    public String encodeWithSha256(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    @Override
    public String encodeWithBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decodeWithBase64(String input) {
        return new String(Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public byte[] encodeHS256(ClientKey key, byte[] message) {
        try {
            return encodeToBytesWithHS256Algorithm(key.getKey().getBytes(StandardCharsets.UTF_8), message);
        } catch (ClientKeyApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encodeHS256(byte[] key, byte[] message) {
        return encodeToBytesWithHS256Algorithm(key, message);
    }

    @Override
    public byte[] encodeSha256(byte[] input) {
        return Hashing.sha256().hashBytes(input).asBytes();
    }

    @Override
    public String encodeBase64(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    @Override
    public byte[] decodeBase64(String input) {
        return Base64.getDecoder().decode(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encodeHex(byte[] input) {
        return DatatypeConverter.printHexBinary(input).toLowerCase();
    }

    @Override
    public byte[] decodeHex(String input) {
        return DatatypeConverter.parseHexBinary(input);
    }

    @Override
    public byte[] toUtf8Bytes(String value) {
        if (value == null) {
            return EMPTY_BYTE_ARRAY;
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String fromUtf8Bytes(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }

    private String encodeWithHS256Algorithm(byte[] keyBytes, String message) {
        return DatatypeConverter.printHexBinary(encodeToBytesWithHS256Algorithm(keyBytes, message.getBytes(UTF_8)))
            .toLowerCase();
    }

    private byte[] encodeToBytesWithHS256Algorithm(byte[] keyBytes, byte[] message) {
        HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, keyBytes);
        return hmac.hmac(message);
    }

}
