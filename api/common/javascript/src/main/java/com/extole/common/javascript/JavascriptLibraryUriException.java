package com.extole.common.javascript;

/**
 * Exception thrown by {@link JavascriptLibraryUriException} to represent that the library URI cannot be processed.
 * This exception allows another {@link JavascriptLibraryProvider} to be used, for example in a
 * {@link CompositeJavascriptLibraryProvider}
 */
public class JavascriptLibraryUriException extends JavascriptLibraryProviderException {

    private static final String MESSAGE_PATTERN = "Invalid URI for javascript library. URI: %s";

    public JavascriptLibraryUriException(String uri, Throwable cause) {
        super(String.format(MESSAGE_PATTERN, uri), cause);
    }
}
