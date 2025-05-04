package com.extole.omissible.types;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.omissible.Omissible;

public class PojoWithFlags {

    private static final String JSON_FLAG = "flag";
    private static final String JSON_JSON_OPTIONAL = "optional_flag";

    private final Omissible<Boolean> flag;
    private final Omissible<Optional<Boolean>> optionalFlag;

    public PojoWithFlags(@JsonProperty(JSON_FLAG) Omissible<Boolean> flag,
        @JsonProperty(JSON_JSON_OPTIONAL) Omissible<Optional<Boolean>> optionalFlag) {
        this.flag = flag;
        this.optionalFlag = optionalFlag;
    }

    @JsonProperty(JSON_FLAG)
    public Omissible<Boolean> getFlag() {
        return flag;
    }

    @JsonProperty(JSON_JSON_OPTIONAL)
    public Omissible<Optional<Boolean>> getOptionalFlag() {
        return optionalFlag;
    }

}
