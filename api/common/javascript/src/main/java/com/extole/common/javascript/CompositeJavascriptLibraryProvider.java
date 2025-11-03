package com.extole.common.javascript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeJavascriptLibraryProvider implements JavascriptLibraryProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeJavascriptLibraryProvider.class);

    private final JavascriptLibraryProvider first;
    private final JavascriptLibraryProvider second;

    public CompositeJavascriptLibraryProvider(JavascriptLibraryProvider first, JavascriptLibraryProvider second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String getLibrary(String uri) throws JavascriptLibraryLoadException, JavascriptLibraryUriException {
        try {
            return first.getLibrary(uri);
        } catch (JavascriptLibraryUriException e) {
            LOG.debug("Library: {} cannot be handled by the provider={}, falling back to provider={}. Error: {}", uri,
                first, second, e.toString());
        }
        return second.getLibrary(uri);
    }

}
