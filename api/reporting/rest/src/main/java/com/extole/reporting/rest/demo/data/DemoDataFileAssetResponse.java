package com.extole.reporting.rest.demo.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DemoDataFileAssetResponse {

    private static final String FILE_ASSET_ID = "file_asset_id";
    private static final String FILE_ASSET_NAME = "file_asset_name";
    private static final String EVENTS_COUNT = "events_count";

    private final String fileAssetId;
    private final String fileAssetName;
    private final int eventsCount;

    public DemoDataFileAssetResponse(@JsonProperty(FILE_ASSET_ID) String fileAssetId,
        @JsonProperty(FILE_ASSET_NAME) String fileAssetName,
        @JsonProperty(EVENTS_COUNT) int eventsCount) {
        this.fileAssetId = fileAssetId;
        this.fileAssetName = fileAssetName;
        this.eventsCount = eventsCount;
    }

    @JsonProperty(FILE_ASSET_ID)
    public String getFileAssetId() {
        return fileAssetId;
    }

    @JsonProperty(FILE_ASSET_NAME)
    public String getFileAssetName() {
        return fileAssetName;
    }

    @JsonProperty(EVENTS_COUNT)
    public int getEventsCount() {
        return eventsCount;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
