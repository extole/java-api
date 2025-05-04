package com.extole.client.rest.prehandler.condition.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = PrehandlerConditionRequest.JSON_TYPE)
@JsonSubTypes({
    @Type(value = EventNameMatchPrehandlerConditionRequest.class, name = EventNameMatchPrehandlerConditionRequest.TYPE),
    @Type(value = HttpHeaderMatchPrehandlerConditionRequest.class,
        name = HttpHeaderMatchPrehandlerConditionRequest.TYPE),
    @Type(value = JavascriptPrehandlerConditionRequest.class, name = JavascriptPrehandlerConditionRequest.TYPE),
    @Type(value = ExpressionPrehandlerConditionRequest.class, name = ExpressionPrehandlerConditionRequest.TYPE),
    @Type(value = DataExistsPrehandlerConditionRequest.class, name = DataExistsPrehandlerConditionRequest.TYPE),
    @Type(value = BlockMatchPrehandlerConditionRequest.class, name = BlockMatchPrehandlerConditionRequest.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = EventNameMatchPrehandlerConditionRequest.TYPE,
        schema = EventNameMatchPrehandlerConditionRequest.class),
    @DiscriminatorMapping(value = HttpHeaderMatchPrehandlerConditionRequest.TYPE,
        schema = HttpHeaderMatchPrehandlerConditionRequest.class),
    @DiscriminatorMapping(value = JavascriptPrehandlerConditionRequest.TYPE,
        schema = JavascriptPrehandlerConditionRequest.class),
    @DiscriminatorMapping(value = ExpressionPrehandlerConditionRequest.TYPE,
        schema = ExpressionPrehandlerConditionRequest.class),
    @DiscriminatorMapping(value = DataExistsPrehandlerConditionRequest.TYPE,
        schema = DataExistsPrehandlerConditionRequest.class),
    @DiscriminatorMapping(value = BlockMatchPrehandlerConditionRequest.TYPE,
        schema = BlockMatchPrehandlerConditionRequest.class)
})
public abstract class PrehandlerConditionRequest {
    static final String JSON_TYPE = "type";

    private final PrehandlerConditionType type;

    protected PrehandlerConditionRequest(PrehandlerConditionType type) {
        this.type = type;
    }

    @JsonProperty(JSON_TYPE)
    @Schema(required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
