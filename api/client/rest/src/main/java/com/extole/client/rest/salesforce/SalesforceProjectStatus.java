package com.extole.client.rest.salesforce;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesforceProjectStatus {
    private static final String JSON_ID = "id";
    private static final String JSON_STATUS = "status";
    private final String id;
    private final String status;

    public SalesforceProjectStatus(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_STATUS) String status) {
        this.id = id;
        this.status = status;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_STATUS)
    public String getStatus() {
        return status;
    }
}
