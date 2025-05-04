package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.client.security.key.ClientKey;

@Schema
public interface EncoderService {

    String safeHtml(String input);

    String safeHtmlContent(String input);

    String safeHtmlAttribute(String input);

    String safeHtmlUnquotedAttribute(String input);

    String safeCssString(String input);

    String safeCssUrl(String input);

    String safeJs(String input);

    String safeJsBlock(String input);

    String safeJsAttribute(String input);

    String safeUriComponent(String input);

    @Deprecated // TODO to be removed ENG-25255
    String encodeWithHS256Algorithm(String key, String message);

    @Deprecated // TODO to be removed ENG-25255
    String encodeWithHS256AlgorithmAndBase64Key(String key, String message);

    @Deprecated // TODO to be removed ENG-25255
    String encodeWithSha256(String input);

    @Deprecated // TODO to be removed ENG-25255
    String encodeWithBase64(String input);

    @Deprecated // TODO to be removed ENG-25255
    String decodeWithBase64(String input);

    byte[] encodeHS256(ClientKey key, byte[] message);

    byte[] encodeHS256(byte[] key, byte[] message);

    byte[] encodeSha256(byte[] input);

    String encodeBase64(byte[] input);

    byte[] decodeBase64(String input);

    String encodeHex(byte[] input);

    byte[] decodeHex(String input);

    byte[] toUtf8Bytes(String value);

    String fromUtf8Bytes(byte[] input);

}
