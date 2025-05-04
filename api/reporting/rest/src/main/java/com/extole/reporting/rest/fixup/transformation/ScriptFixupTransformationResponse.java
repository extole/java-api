package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScriptFixupTransformationResponse extends FixupTransformationResponse {

    private static final String JSON_SCRIPT = "script";

    private final String script;

    @JsonCreator
    public ScriptFixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type,
        @JsonProperty(JSON_SCRIPT) String container) {
        super(id, type);
        this.script = container;
    }

    public String getScript() {
        return script;
    }
}
