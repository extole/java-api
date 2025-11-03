package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.client.rest.campaign.component.setting.SocketFilterType;
import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = SocketFilterConfiguration.TYPE,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = ComponentTypeSocketFilterConfiguration.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComponentTypeSocketFilterConfiguration.class,
        name = ComponentTypeSocketFilterConfiguration.TYPE),
    @JsonSubTypes.Type(value = ComponentFacetSocketFilterConfiguration.class,
        name = ComponentFacetSocketFilterConfiguration.TYPE),
})
public abstract class SocketFilterConfiguration {

    static final String TYPE = "type";

    private final SocketFilterType type;

    protected SocketFilterConfiguration(@JsonProperty(TYPE) SocketFilterType type) {
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
