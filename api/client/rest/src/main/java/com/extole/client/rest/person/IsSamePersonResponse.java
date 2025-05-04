package com.extole.client.rest.person;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IsSamePersonResponse {
    private static final String IS_SAME = "is_same";
    private final Boolean isSame;

    public IsSamePersonResponse(@JsonProperty(IS_SAME) Boolean isSame) {
        this.isSame = isSame;
    }

    @JsonProperty(IS_SAME)
    public Boolean isSame() {
        return isSame;
    }
}
