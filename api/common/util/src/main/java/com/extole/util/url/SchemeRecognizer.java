package com.extole.util.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SchemeRecognizer {
    /**
     * Scheme is defined http://tools.ietf.org/html/rfc3986#section-3.1 scheme =
     * ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
     */
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^[\\p{Alpha}]{1}[\\p{Alpha}\\p{Digit}+-.]*://.+$");

    private SchemeRecognizer() {
    }

    public static boolean hasScheme(String url) {

        Matcher schemeMatcher = SCHEME_PATTERN.matcher(url);
        return schemeMatcher.matches();

    }

    public static String addSchemeToUrl(String url) {

        if (!hasScheme(url)) {
            url = "scheme://" + url;
        }

        return url;
    }

    public static String removeSchemeFromUrl(String url) {
        if (hasScheme(url)) {
            url = url.substring(url.indexOf("://") + "://".length());
        }

        return url;
    }
}
