package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScriptFixupTransformationRequest {
    private static final String JSON_SCRIPT = "script";

    private final String script;

    @JsonCreator
    public ScriptFixupTransformationRequest(@JsonProperty(JSON_SCRIPT) String script) {
        this.script = script;
    }

    @JsonProperty(JSON_SCRIPT)
    public String getScript() {
        return script;
    }
}
