package com.extole.common.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuccessResponse {
    private static final String SUCCESS_VALUE = "success";
    public static final SuccessResponse SUCCESS = new SuccessResponse(SUCCESS_VALUE);

    public SuccessResponse(@JsonProperty("status") String success) {
    }

    @JsonProperty("status")
    public String getStatus() {
        return SUCCESS_VALUE;
    }

    public static SuccessResponse getInstance() {
        return SUCCESS;
    }
}
