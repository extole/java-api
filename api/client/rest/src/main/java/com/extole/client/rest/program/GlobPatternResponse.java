package com.extole.client.rest.program;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobPatternResponse {
    private static final String PATTERN = "pattern";
    private static final String REGEX = "regex";
    private static final String TYPE = "type";
    private final String pattern;
    private final String regex;
    private final ProgramSitePatternType type;

    public GlobPatternResponse(@JsonProperty(PATTERN) String pattern, @JsonProperty(REGEX) String regex,
        @JsonProperty(TYPE) ProgramSitePatternType type) {
        this.pattern = pattern;
        this.regex = regex;
        this.type = type;
    }

    @JsonProperty(PATTERN)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(REGEX)
    public String getRegex() {
        return regex;
    }

    @JsonProperty(TYPE)
    public ProgramSitePatternType getType() {
        return type;
    }

}
