package com.extole.encoder;

import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;

public final class Encoder {
    private static final Encoder INSTANCE = new Encoder();
    private static final String SCRIPT_TAG_REGEX = "<script\\b[^<]*(?:(?!</script>)<[^<]*)*</script>";

    private Encoder() {
    }

    public static Encoder getInstance() {
        return INSTANCE;
    }

    public String safeHtml(String value) {
        if (value == null) {
            return value;
        }

        String changedValue = value.replaceAll(SCRIPT_TAG_REGEX, StringUtils.EMPTY);
        return Encode.forHtml(changedValue);
    }

    public String safeHtmlContent(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forHtmlContent(value);
    }

    public String safeHtmlAttribute(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forHtmlAttribute(value);
    }

    public String safeHtmlUnquotedAttribute(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forHtmlUnquotedAttribute(value);
    }

    public String safeJavaScriptAttribute(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forJavaScriptAttribute(value);
    }

    public String safeJavaScriptBlock(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forJavaScriptBlock(value);
    }

    public String safeJavaScript(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forJavaScript(value);
    }

    public String safeUriComponent(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forUriComponent(value);
    }

    public String safeCssString(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forCssString(value);
    }

    public String safeCssUrl(String value) {
        if (value == null) {
            return value;
        }
        return Encode.forCssUrl(value);
    }

}
