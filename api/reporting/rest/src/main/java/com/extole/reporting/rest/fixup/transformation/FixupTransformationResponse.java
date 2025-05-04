package com.extole.reporting.rest.fixup.transformation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = FixupTransformationResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScriptFixupTransformationResponse.class, name = "SCRIPT"),
    @JsonSubTypes.Type(value = ContainerFixupTransformationResponse.class, name = "CONTAINER"),
    @JsonSubTypes.Type(value = PiiObfuscateFixupTransformationResponse.class, name = "PII_OBFUSCATE"),
    @JsonSubTypes.Type(value = ConditionalAliasChangeFixupTransformationResponse.class,
        name = "CONDITIONAL_ALIAS_CHANGE"),
    @JsonSubTypes.Type(value = ProgramLabelCampaignFixupTransformationResponse.class, name = "PROGRAM_LABEL_CAMPAIGN")
})
public class FixupTransformationResponse {
    static final String JSON_ID = "id";
    static final String JSON_TYPE = "type";

    private final String id;
    private final FixupTransformationType type;

    @JsonCreator
    public FixupTransformationResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupTransformationType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_TYPE)
    public FixupTransformationType getType() {
        return type;
    }
}
