package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

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

    String encodeWithHS256Algorithm(String key, String message);

    String encodeWithHS256AlgorithmAndBase64Key(String key, String message);

    String encodeWithSha256(String input);

    String encodeWithBase64(String input);

    String decodeWithBase64(String input);
}
