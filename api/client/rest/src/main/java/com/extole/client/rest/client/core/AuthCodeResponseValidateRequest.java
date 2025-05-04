package com.extole.client.rest.client.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthCodeResponseValidateRequest {

    private static final String JSON_CODE = "code";
    private static final String JSON_STATE = "state";

    private final String code;
    private final String state;

    @JsonCreator
    public AuthCodeResponseValidateRequest(@JsonProperty(JSON_CODE) String code,
        @JsonProperty(JSON_STATE) String state) {
        this.code = code;
        this.state = state;
    }

    @JsonProperty(JSON_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_STATE)
    public String getState() {
        return state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String code;
        private String state;

        private Builder() {

        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public AuthCodeResponseValidateRequest build() {
            return new AuthCodeResponseValidateRequest(code,
                state);
        }
    }

}
