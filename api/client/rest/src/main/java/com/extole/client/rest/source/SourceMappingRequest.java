package com.extole.client.rest.source;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class SourceMappingRequest {
    private static final String PROGRAM_LABEL = "program_label";
    private static final String MAPPING_FROM = "source_from";
    private static final String MAPPING_TO = "source_to";

    private final Omissible<String> programLabel;
    private final Omissible<String> sourceFrom;
    private final Omissible<String> sourceTo;

    SourceMappingRequest(@JsonProperty(PROGRAM_LABEL) Omissible<String> programLabel,
        @JsonProperty(MAPPING_FROM) Omissible<String> sourceFrom,
        @JsonProperty(MAPPING_TO) Omissible<String> sourceTo) {
        this.programLabel = programLabel;
        this.sourceFrom = sourceFrom;
        this.sourceTo = sourceTo;
    }

    @JsonProperty(PROGRAM_LABEL)
    public Omissible<String> getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(MAPPING_FROM)
    public Omissible<String> getSourceFrom() {
        return sourceFrom;
    }

    @JsonProperty(MAPPING_TO)
    public Omissible<String> getSourceTo() {
        return sourceTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Omissible<String> programLabel = Omissible.omitted();
        private Omissible<String> sourceFrom = Omissible.omitted();
        private Omissible<String> sourceTo = Omissible.omitted();

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = Omissible.of(programLabel);
            return this;
        }

        public Builder withSourceFrom(String sourceFrom) {
            this.sourceFrom = Omissible.of(sourceFrom);
            return this;
        }

        public Builder withSourceTo(String sourceTo) {
            this.sourceTo = Omissible.of(sourceTo);
            return this;
        }

        public SourceMappingRequest build() {
            return new SourceMappingRequest(programLabel, sourceFrom, sourceTo);
        }
    }
}
