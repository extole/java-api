package com.extole.common.javascript;

/**
 * Base exception thrown by {@link JavascriptLibraryProvider}
 */
public abstract class JavascriptLibraryProviderException extends Exception {

    public JavascriptLibraryProviderException(String message) {
        super(message);
    }

    public JavascriptLibraryProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
