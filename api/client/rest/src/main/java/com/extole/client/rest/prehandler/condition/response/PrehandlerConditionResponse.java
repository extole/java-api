package com.extole.client.rest.prehandler.condition.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = PrehandlerConditionResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = EventNameMatchPrehandlerConditionResponse.class,
        name = EventNameMatchPrehandlerConditionResponse.TYPE),
    @Type(value = HttpHeaderMatchPrehandlerConditionResponse.class,
        name = HttpHeaderMatchPrehandlerConditionResponse.TYPE),
    @Type(value = JavascriptPrehandlerConditionResponse.class, name = JavascriptPrehandlerConditionResponse.TYPE),
    @Type(value = ExpressionPrehandlerConditionResponse.class, name = ExpressionPrehandlerConditionResponse.TYPE),
    @Type(value = DataExistsPrehandlerConditionResponse.class, name = DataExistsPrehandlerConditionResponse.TYPE),
    @Type(value = BlockMatchPrehandlerConditionResponse.class, name = BlockMatchPrehandlerConditionResponse.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = EventNameMatchPrehandlerConditionResponse.TYPE,
        schema = EventNameMatchPrehandlerConditionResponse.class),
    @DiscriminatorMapping(value = HttpHeaderMatchPrehandlerConditionResponse.TYPE,
        schema = HttpHeaderMatchPrehandlerConditionResponse.class),
    @DiscriminatorMapping(value = JavascriptPrehandlerConditionResponse.TYPE,
        schema = JavascriptPrehandlerConditionResponse.class),
    @DiscriminatorMapping(value = ExpressionPrehandlerConditionResponse.TYPE,
        schema = ExpressionPrehandlerConditionResponse.class),
    @DiscriminatorMapping(value = DataExistsPrehandlerConditionResponse.TYPE,
        schema = DataExistsPrehandlerConditionResponse.class),
    @DiscriminatorMapping(value = BlockMatchPrehandlerConditionResponse.TYPE,
        schema = BlockMatchPrehandlerConditionResponse.class)
})
public abstract class PrehandlerConditionResponse {
    static final String JSON_ID = "id";
    static final String JSON_TYPE = "type";

    private final String id;
    private final PrehandlerConditionType type;

    protected PrehandlerConditionResponse(String id, PrehandlerConditionType type) {
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
    public PrehandlerConditionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
