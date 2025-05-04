package com.extole.client.rest.logo;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class LogoResponse {

    private static final String LOGO_ID = "logo_id";
    private static final String CREATED_DATE = "created_date";
    private static final String UPDATED_DATE = "updated_date";

    private final String logoId;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    public LogoResponse(@JsonProperty(LOGO_ID) String logoId,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate) {
        this.logoId = logoId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(LOGO_ID)
    public String getId() {
        return logoId;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
