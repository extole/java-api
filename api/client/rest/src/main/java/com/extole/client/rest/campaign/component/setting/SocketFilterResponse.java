package com.extole.client.rest.campaign.component.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = SocketFilterResponse.TYPE,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = ComponentTypeSocketFilterResponse.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComponentTypeSocketFilterResponse.class,
        name = ComponentTypeSocketFilterResponse.TYPE),
    @JsonSubTypes.Type(value = ComponentFacetSocketFilterResponse.class,
        name = ComponentFacetSocketFilterResponse.TYPE),
})
public abstract class SocketFilterResponse {

    static final String TYPE = "type";

    private final SocketFilterType type;

    protected SocketFilterResponse(@JsonProperty(TYPE) SocketFilterType type) {
        this.type = type;
    }

    @JsonProperty(TYPE)
    public SocketFilterType getType() {
        return type;
    }

}
