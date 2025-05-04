package com.extole.client.rest.prehandler.action.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = PrehandlerActionRequest.JSON_TYPE)
@JsonSubTypes({
    @Type(value = SetDataPrehandlerActionRequest.class, name = SetDataPrehandlerActionRequest.TYPE),
    @Type(value = SetSandboxPrehandlerActionRequest.class, name = SetSandboxPrehandlerActionRequest.TYPE),
    @Type(value = JavascriptPrehandlerActionRequest.class, name = JavascriptPrehandlerActionRequest.TYPE),
    @Type(value = MapDataAttributesPrehandlerActionRequest.class, name = MapDataAttributesPrehandlerActionRequest.TYPE),
    @Type(value = ExpressionPrehandlerActionRequest.class, name = ExpressionPrehandlerActionRequest.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = SetDataPrehandlerActionRequest.TYPE,
        schema = SetDataPrehandlerActionRequest.class),
    @DiscriminatorMapping(value = SetSandboxPrehandlerActionRequest.TYPE,
        schema = SetSandboxPrehandlerActionRequest.class),
    @DiscriminatorMapping(value = JavascriptPrehandlerActionRequest.TYPE,
        schema = JavascriptPrehandlerActionRequest.class),
    @DiscriminatorMapping(value = MapDataAttributesPrehandlerActionRequest.TYPE,
        schema = MapDataAttributesPrehandlerActionRequest.class),
    @DiscriminatorMapping(value = ExpressionPrehandlerActionRequest.TYPE,
        schema = ExpressionPrehandlerActionRequest.class)
})
public abstract class PrehandlerActionRequest {
    static final String JSON_TYPE = "type";

    private final PrehandlerActionType type;

    protected PrehandlerActionRequest(PrehandlerActionType type) {
        this.type = type;
    }

    @JsonProperty(JSON_TYPE)
    @Schema(required = true, nullable = false)
    public PrehandlerActionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
