package com.extole.omissible.types;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ConcretePojo extends AbstractPojo {

    public ConcretePojo(@JsonProperty(JSON_SIMPLE_POJO) Omissible<Optional<SimplePojo>> simplePojo) {
        super("CONCRETE", simplePojo);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
