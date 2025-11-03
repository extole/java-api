package com.extole.common.javascript;

public interface JavascriptLibraryLoader {
    String VARIABLE_NAME = "ExtoleJavascriptLibraryLoader";

    String getNormalizedUri(String uri);

    String getNormalizedName(String uri);

    String get(String uri) throws JavascriptLibraryLoaderException;
}
