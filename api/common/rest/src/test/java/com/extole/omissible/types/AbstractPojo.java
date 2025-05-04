package com.extole.omissible.types;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AbstractPojo.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ConcretePojo.class, name = "CONCRETE"),
})
public abstract class AbstractPojo {
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_SIMPLE_POJO = "simple_pojo";

    private final String type;
    private final Omissible<Optional<SimplePojo>> simplePojo;

    public AbstractPojo(String type, Omissible<Optional<SimplePojo>> simplePojo) {
        this.type = type;
        this.simplePojo = simplePojo;
    }

    @JsonProperty(JSON_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_SIMPLE_POJO)
    public Omissible<Optional<SimplePojo>> getSimplePojo() {
        return simplePojo;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
