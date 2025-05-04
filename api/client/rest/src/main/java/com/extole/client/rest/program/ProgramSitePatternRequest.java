package com.extole.client.rest.program;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProgramSitePatternRequest {

    private static final String SITE_PATTERN = "site_pattern";
    private static final String TYPE = "type";

    private final String sitePattern;
    private final Optional<ProgramSitePatternType> type;

    public ProgramSitePatternRequest(@JsonProperty(SITE_PATTERN) String pattern,
        @Nullable @JsonProperty(TYPE) ProgramSitePatternType type) {
        this.sitePattern = pattern;
        this.type = Optional.ofNullable(type);
    }

    @JsonProperty(SITE_PATTERN)
    public String getSitePattern() {
        return sitePattern;
    }

    @JsonProperty(TYPE)
    public Optional<ProgramSitePatternType> getType() {
        return type;
    }

    public static final ProgramSitePatternRequest.Builder builder() {
        return new ProgramSitePatternRequest.Builder();
    }

    public static final class Builder {

        private String sitePattern;
        private ProgramSitePatternType type;

        private Builder() {
        }

        public Builder withSitePattern(String sitePattern) {
            this.sitePattern = sitePattern;
            return this;
        }

        public Builder withType(ProgramSitePatternType type) {
            this.type = type;
            return this;
        }

        public ProgramSitePatternRequest build() {
            return new ProgramSitePatternRequest(sitePattern, type);
        }
    }
}
