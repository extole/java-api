package com.extole.consumer.rest.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentName {
    private static final String JSON_TEXT = "text";
    private static final String JSON_LANGUAGE_CODE = "language_code";

    private final String text;
    private final String languageCode;

    public ComponentName(
        @JsonProperty(JSON_TEXT) String text,
        @JsonProperty(JSON_LANGUAGE_CODE) String languageCode) {
        this.text = text;
        this.languageCode = languageCode;
    }

    @JsonProperty(JSON_TEXT)
    public String getText() {
        return text;
    }

    @JsonProperty(JSON_LANGUAGE_CODE)
    public String getLanguageCode() {
        return languageCode;
    }
}
