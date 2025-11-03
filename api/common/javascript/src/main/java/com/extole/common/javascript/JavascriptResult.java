package com.extole.common.javascript;

import javax.annotation.Nullable;

import org.openjdk.nashorn.api.scripting.JSObject;

public final class JavascriptResult {
    private final String scriptSnippet;
    private final JSObject jsObject;
    private final Object value;

    private JavascriptResult(String scriptSnippet, @Nullable JSObject jsObject, @Nullable Object value) {
        this.scriptSnippet = scriptSnippet;
        this.jsObject = jsObject;
        this.value = value;
    }

    public static JavascriptResult newInstance(String scriptSnippet, @Nullable Object object) {
        JSObject jsObject = null;
        Object value = null;
        if (object instanceof JSObject) {
            jsObject = (JSObject) object;
        } else {
            value = object;
        }
        return new JavascriptResult(scriptSnippet, jsObject, value);
    }

    public String getScriptSnippet() {
        return scriptSnippet;
    }

    @Nullable
    public String getMemberAsString(String name) {
        if (jsObject == null) {
            return null;
        }
        Object member = jsObject.getMember(name);
        return member != null ? member.toString() : null;
    }

    @Nullable
    public <T> T getMember(String name) {
        if (jsObject == null) {
            return null;
        }
        return (T) jsObject.getMember(name);
    }

    @Nullable
    <T> T call(Object... arguments) {
        if (jsObject == null) {
            return null;
        }
        return (T) jsObject.call(null, arguments);
    }

    @Nullable
    Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "JavascriptResult[script: " + scriptSnippet + ", value: " + value + "]";
    }
}
