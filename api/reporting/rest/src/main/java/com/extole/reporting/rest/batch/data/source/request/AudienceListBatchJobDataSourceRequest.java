package com.extole.reporting.rest.batch.data.source.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;

public class AudienceListBatchJobDataSourceRequest extends BatchJobDataSourceRequest {
    static final String DATA_SOURCE_TYPE = "AUDIENCE_LIST";

    private static final String AUDIENCE_LIST_ID = "audience_list_id";

    private final String audienceListId;

    public AudienceListBatchJobDataSourceRequest(@JsonProperty(AUDIENCE_LIST_ID) String audienceListId) {
        super(BatchJobDataSourceType.AUDIENCE_LIST);
        this.audienceListId = audienceListId;
    }

    @JsonProperty(AUDIENCE_LIST_ID)
    public String getAudienceListId() {
        return audienceListId;
    }
}
