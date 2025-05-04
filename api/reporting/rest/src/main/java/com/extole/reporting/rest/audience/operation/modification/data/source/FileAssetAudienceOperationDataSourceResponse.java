package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.api.file.asset.FileAsset;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class FileAssetAudienceOperationDataSourceResponse extends AudienceOperationDataSourceResponse {

    public static final String DATA_SOURCE_TYPE = "FILE_ASSET";

    private static final String NAME = "name";
    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";
    private static final String FILE_ASSET_ID = "file_asset_id";

    private final String name;
    private final Set<String> eventColumns;
    private final Map<String, String> eventData;
    private final Id<FileAsset> fileAssetId;

    public FileAssetAudienceOperationDataSourceResponse(
        @JsonProperty(NAME) String name,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(FILE_ASSET_ID) Id<FileAsset> fileAssetId) {
        super(AudienceOperationDataSourceType.FILE_ASSET);
        this.name = name;
        this.eventColumns = ImmutableSet.copyOf(eventColumns);
        this.eventData = ImmutableMap.copyOf(eventData);
        this.fileAssetId = fileAssetId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Set<String> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Map<String, String> getEventData() {
        return eventData;
    }

    @JsonProperty(FILE_ASSET_ID)
    public Id<FileAsset> getFileAssetId() {
        return fileAssetId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
