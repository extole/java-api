package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = SocketFilterCreateRequest.TYPE,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = ComponentTypeSocketFilterCreateRequest.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComponentTypeSocketFilterCreateRequest.class,
        name = ComponentTypeSocketFilterCreateRequest.TYPE),
    @JsonSubTypes.Type(value = ComponentFacetSocketFilterCreateRequest.class,
        name = ComponentFacetSocketFilterCreateRequest.TYPE),
})
public abstract class SocketFilterCreateRequest {

    static final String TYPE = "type";

    private final SocketFilterType type;

    protected SocketFilterCreateRequest(@JsonProperty(TYPE) SocketFilterType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public SocketFilterType getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
