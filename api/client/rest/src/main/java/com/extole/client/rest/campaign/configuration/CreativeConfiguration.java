package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CreativeConfiguration {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CLASSIFICATION = "classification";
    private static final String OUTPUT = "output";
    private static final String API_VERSION = "api_version";
    private static final String THEME_VERSION = "theme_version";
    private static final String LOCALES = "locales";
    private static final String AVAILABLE_LOCALES = "available_locales";
    private static final String DEFAULT_LOCALE = "default_locale";

    private final String name;
    private final String id;
    private final String classification;
    private final Integer apiVersion;
    private final String themeVersion;
    private final Map<String, List<String>> output;
    private final List<String> locales;
    private final List<String> availableLocales;
    private final String defaultLocale;

    public CreativeConfiguration(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(CLASSIFICATION) String classification,
        @Nullable @JsonProperty(OUTPUT) Map<String, List<String>> output,
        @JsonProperty(API_VERSION) Integer apiVersion,
        @JsonProperty(THEME_VERSION) String themeVersion,
        @JsonProperty(LOCALES) List<String> locales,
        @JsonProperty(AVAILABLE_LOCALES) List<String> availableLocales,
        @JsonProperty(DEFAULT_LOCALE) String defaultLocale) {
        this.id = id;
        this.name = name;
        this.classification = classification;
        this.output = output;
        this.apiVersion = apiVersion;
        this.themeVersion = themeVersion;
        this.locales = locales;
        this.defaultLocale = defaultLocale;
        this.availableLocales = availableLocales;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(CLASSIFICATION)
    public String getClassification() {
        return classification;
    }

    @Nullable
    @JsonProperty(OUTPUT)
    public Map<String, List<String>> getOutput() {
        return output;
    }

    @JsonProperty(API_VERSION)
    public Integer getApiVersion() {
        return apiVersion;
    }

    @JsonProperty(THEME_VERSION)
    public String getThemeVersion() {
        return themeVersion;
    }

    @JsonProperty(LOCALES)
    public List<String> getLocales() {
        return locales;
    }

    @JsonProperty(AVAILABLE_LOCALES)
    public List<String> getAvailableLocales() {
        return availableLocales;
    }

    @JsonProperty(DEFAULT_LOCALE)
    public String getDefaultLocale() {
        return defaultLocale;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
