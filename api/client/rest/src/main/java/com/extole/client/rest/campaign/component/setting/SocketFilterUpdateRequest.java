package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = SocketFilterUpdateRequest.TYPE,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = ComponentTypeSocketFilterUpdateRequest.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComponentTypeSocketFilterUpdateRequest.class,
        name = ComponentTypeSocketFilterUpdateRequest.TYPE),
    @JsonSubTypes.Type(value = ComponentFacetSocketFilterUpdateRequest.class,
        name = ComponentFacetSocketFilterUpdateRequest.TYPE),
})
public abstract class SocketFilterUpdateRequest {

    static final String TYPE = "type";

    private final SocketFilterType type;

    protected SocketFilterUpdateRequest(@JsonProperty(TYPE) SocketFilterType type) {
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
