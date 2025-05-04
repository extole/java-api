package com.extole.client.rest.program;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProgramSitePatternResponse {

    private static final String ID = "id";
    private static final String SITE_PATTERN = "site_pattern";
    private static final String TYPE = "type";

    private final String id;
    private final String sitePattern;
    private final ProgramSitePatternType type;

    public ProgramSitePatternResponse(@JsonProperty(ID) String id,
        @JsonProperty(SITE_PATTERN) String pattern,
        @JsonProperty(TYPE) ProgramSitePatternType type) {
        this.id = id;
        this.sitePattern = pattern;
        this.type = type;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(SITE_PATTERN)
    public String getSitePattern() {
        return sitePattern;
    }

    @JsonProperty(TYPE)
    public ProgramSitePatternType getType() {
        return type;
    }

}
