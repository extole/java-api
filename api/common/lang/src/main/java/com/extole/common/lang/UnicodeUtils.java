package com.extole.common.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.glaforge.i18n.io.CharsetToolkit;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UnicodeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UnicodeUtils.class);

    private static final String UNICODE_SURROGATE_REGEX = "[^\u0000-\uFFFF]";
    private static final String DEFAULT_SURROGATE_REPLACEMENT = "?";
    private static final int GUESSENCODING_BUFFER = 4096;

    private UnicodeUtils() {
    }

    public static String replaceNonUTF8Characters(String input) {
        return replaceNonUTF8Characters(input, DEFAULT_SURROGATE_REPLACEMENT);
    }

    public static String replaceNonUTF8Characters(String input, String replacement) {
        String replacedInput = input.replaceAll(UNICODE_SURROGATE_REGEX, replacement);
        if (!Strings.isNullOrEmpty(input) && Strings.isNullOrEmpty(replacedInput)) {
            return DEFAULT_SURROGATE_REPLACEMENT;
        } else {
            return replacedInput;
        }
    }

    public static Charset detectCharset(File file) {
        try {
            return CharsetToolkit.guessEncoding(file, GUESSENCODING_BUFFER, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Cannot detect encoding for file: " + file, e);
            return StandardCharsets.UTF_8;
        }
    }
}
