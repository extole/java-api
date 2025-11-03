package com.extole.common.javascript;

/**
 * Exception thrown by {@link JavascriptLibraryProvider} to represent that the library cannot be loaded.
 */
public class JavascriptLibraryLoadException extends JavascriptLibraryProviderException {

    public JavascriptLibraryLoadException(String message) {
        super(message);
    }

    public JavascriptLibraryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
