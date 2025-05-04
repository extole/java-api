package com.extole.client.rest.prehandler.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = PrehandlerActionResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = SetDataPrehandlerActionResponse.class, name = SetDataPrehandlerActionResponse.TYPE),
    @Type(value = SetSandboxPrehandlerActionResponse.class, name = SetSandboxPrehandlerActionResponse.TYPE),
    @Type(value = JavascriptPrehandlerActionResponse.class, name = JavascriptPrehandlerActionResponse.TYPE),
    @Type(value = MapDataAttributesPrehandlerActionResponse.class,
        name = MapDataAttributesPrehandlerActionResponse.TYPE),
    @Type(value = ExpressionPrehandlerActionResponse.class, name = ExpressionPrehandlerActionResponse.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = SetDataPrehandlerActionResponse.TYPE,
        schema = SetDataPrehandlerActionResponse.class),
    @DiscriminatorMapping(value = SetSandboxPrehandlerActionResponse.TYPE,
        schema = SetSandboxPrehandlerActionResponse.class),
    @DiscriminatorMapping(value = JavascriptPrehandlerActionResponse.TYPE,
        schema = JavascriptPrehandlerActionResponse.class),
    @DiscriminatorMapping(value = MapDataAttributesPrehandlerActionResponse.TYPE,
        schema = MapDataAttributesPrehandlerActionResponse.class),
    @DiscriminatorMapping(value = ExpressionPrehandlerActionResponse.TYPE,
        schema = ExpressionPrehandlerActionResponse.class)
})
public abstract class PrehandlerActionResponse {
    static final String JSON_ID = "id";
    static final String JSON_TYPE = "type";

    private final String id;
    private final PrehandlerActionType type;

    protected PrehandlerActionResponse(String id, PrehandlerActionType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty(JSON_ID)
    @Schema(nullable = false)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_TYPE)
    @Schema(nullable = false)
    public PrehandlerActionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
