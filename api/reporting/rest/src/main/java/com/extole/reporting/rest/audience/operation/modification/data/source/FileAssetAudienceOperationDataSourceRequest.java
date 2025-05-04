package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.file.asset.FileAsset;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class FileAssetAudienceOperationDataSourceRequest extends AudienceOperationDataSourceRequest {

    public static final String DATA_SOURCE_TYPE = "FILE_ASSET";

    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";
    private static final String FILE_ASSET_ID = "file_asset_id";

    private final Omissible<Set<String>> eventColumns;
    private final Omissible<Map<String, String>> eventData;
    private final Id<FileAsset> fileAssetId;

    public FileAssetAudienceOperationDataSourceRequest(@JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData,
        @JsonProperty(FILE_ASSET_ID) Id<FileAsset> fileAssetId) {
        super(AudienceOperationDataSourceType.FILE_ASSET);
        this.eventColumns = eventColumns;
        this.eventData = eventData;
        this.fileAssetId = fileAssetId;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Omissible<Set<String>> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Omissible<Map<String, String>> getEventData() {
        return eventData;
    }

    @JsonProperty(FILE_ASSET_ID)
    public Id<FileAsset> getFileAssetId() {
        return fileAssetId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private Id<FileAsset> fileAssetId;

        private Builder() {

        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public Builder withFileAssetId(Id<FileAsset> fileAssetId) {
            this.fileAssetId = fileAssetId;
            return this;
        }

        public FileAssetAudienceOperationDataSourceRequest build() {
            return new FileAssetAudienceOperationDataSourceRequest(eventColumns, eventData, fileAssetId);
        }

    }

}
