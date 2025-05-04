package com.extole.client.rest.reward.supplier.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouponFileResponse {

    private static final String COUPON_FILE_ID = "id";
    private static final String FILE_NAME = "file_name";

    private final String id;
    private final String fileName;

    public CouponFileResponse(@JsonProperty(COUPON_FILE_ID) String id, @JsonProperty(FILE_NAME) String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    @JsonProperty(COUPON_FILE_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(FILE_NAME)
    public String getFileName() {
        return fileName;
    }
}
