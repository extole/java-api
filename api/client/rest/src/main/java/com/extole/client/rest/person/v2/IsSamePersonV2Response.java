package com.extole.client.rest.person.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IsSamePersonV2Response {
    private static final String IS_SAME = "is_same";
    private final Boolean isSame;

    public IsSamePersonV2Response(@JsonProperty(IS_SAME) Boolean isSame) {
        this.isSame = isSame;
    }

    @JsonProperty(IS_SAME)
    public Boolean isSame() {
        return isSame;
    }
}
