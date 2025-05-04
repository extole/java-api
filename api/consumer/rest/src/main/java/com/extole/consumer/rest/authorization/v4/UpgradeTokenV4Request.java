package com.extole.consumer.rest.authorization.v4;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO REMOVE ENG-9666
public class UpgradeTokenV4Request {
    private final String extoleSecret;

    @JsonCreator
    public UpgradeTokenV4Request(@JsonProperty("extole_secret") String extoleSecret) {
        this.extoleSecret = extoleSecret;
    }

    @Nullable
    @JsonProperty("extole_secret")
    public String getExtoleSecret() {
        return extoleSecret;
    }
}
