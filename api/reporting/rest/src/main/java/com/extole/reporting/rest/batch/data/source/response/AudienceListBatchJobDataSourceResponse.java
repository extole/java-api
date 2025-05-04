package com.extole.reporting.rest.batch.data.source.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

public class AudienceListBatchJobDataSourceResponse extends BatchJobDataSourceResponse {
    static final String DATA_SOURCE_TYPE = "AUDIENCE_LIST";

    private static final String AUDIENCE_LIST_ID = "audience_list_id";

    private final String audienceListId;

    public AudienceListBatchJobDataSourceResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(AUDIENCE_LIST_ID) String audienceListId) {
        super(id, BatchJobDataSourceType.AUDIENCE_LIST);
        this.audienceListId = audienceListId;
    }

    @JsonProperty(AUDIENCE_LIST_ID)
    public String getAudienceListId() {
        return audienceListId;
    }
}
