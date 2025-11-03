package com.extole.common.javascript;

public interface JavascriptLibraryProvider {

    /**
     * Provides the library with the given URI.
     *
     * @param uri - URI of the library to be provided
     * @return library content
     * @throws JavascriptLibraryLoadException in case the library cannot be loaded
     * @throws JavascriptLibraryUriException in case the library URI cannot be processed. It allows another
     *             {@link JavascriptLibraryProvider} to be used, for example in a
     *             {@link CompositeJavascriptLibraryProvider}
     */
    String getLibrary(String uri) throws JavascriptLibraryLoadException, JavascriptLibraryUriException;

}
