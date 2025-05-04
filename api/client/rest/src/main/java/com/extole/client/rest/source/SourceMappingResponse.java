package com.extole.client.rest.source;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceMappingResponse {
    private static final String ID = "id";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String SOURCE_FROM = "source_from";
    private static final String SOURCE_TO = "source_to";

    private final String id;
    private final String programLabel;
    private final String sourceFrom;
    private final String sourceTo;

    public SourceMappingResponse(@JsonProperty(ID) String id,
        @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(SOURCE_FROM) String sourceFrom,
        @JsonProperty(SOURCE_TO) String sourceTo) {
        this.id = id;
        this.programLabel = programLabel;
        this.sourceFrom = sourceFrom;
        this.sourceTo = sourceTo;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(SOURCE_FROM)
    public String getSourceFrom() {
        return sourceFrom;
    }

    @JsonProperty(SOURCE_TO)
    public String getSourceTo() {
        return sourceTo;
    }
}
