package com.extole.client.rest.shareable.v2;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
public class ConsumerEventV2Request {
    private static final String SOURCE_PARAM = "source";

    private final String source;

    public ConsumerEventV2Request(@JsonProperty(value = SOURCE_PARAM, required = false) String source) {
        this.source = source;
    }

    @Nullable
    @JsonProperty(value = SOURCE_PARAM, required = false)
    public String getSource() {
        return source;
    }

}
