package com.extole.reporting.rest.audience.list.response;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class UploadedAudienceListResponse extends AudienceListResponse {
    static final String AUDIENCE_TYPE = "UPLOADED";

    private static final String FILE_ASSET_ID = "file_asset_id";
    private static final String AUDIENCE_ID = "audience_id";
    private static final String INPUT_ROWS_COUNT = "input_rows_count";
    private static final String ANONYMOUS_COUNT = "anonymous_count";
    private static final String NON_PROCESSED_COUNT = "non_processed_count";

    private final String fileAssetId;
    private final Optional<String> audienceId;
    private final Optional<Long> inputRowsCount;
    private final Optional<Long> anonymousCount;
    private final Optional<Long> nonProcessedCount;

    public UploadedAudienceListResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(STATE) AudienceListState audienceListState,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(FILE_ASSET_ID) String fileAssetId,
        @JsonProperty(AUDIENCE_ID) Optional<String> audienceId,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(MEMBER_COUNT) Optional<Long> memberCount,
        @JsonProperty(INPUT_ROWS_COUNT) Optional<Long> inputRowsCount,
        @JsonProperty(ANONYMOUS_COUNT) Optional<Long> anonymousCount,
        @JsonProperty(NON_PROCESSED_COUNT) Optional<Long> nonProcessedCount,
        @JsonProperty(LAST_UPDATE) Optional<ZonedDateTime> lastUpdate,
        @JsonProperty(ERROR_CODE) Optional<String> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage) {
        super(AudienceListType.UPLOADED, id, name, tags, audienceListState, description, eventColumns, eventData,
            memberCount, lastUpdate, errorCode, errorMessage);
        this.fileAssetId = fileAssetId;
        this.audienceId = audienceId;
        this.inputRowsCount = inputRowsCount;
        this.anonymousCount = anonymousCount;
        this.nonProcessedCount = nonProcessedCount;
    }

    @JsonProperty(FILE_ASSET_ID)
    public String getFileAssetId() {
        return fileAssetId;
    }

    @JsonProperty(AUDIENCE_ID)
    public Optional<String> getAudienceId() {
        return audienceId;
    }

    @JsonProperty(INPUT_ROWS_COUNT)
    public Optional<Long> getInputRowsCount() {
        return inputRowsCount;
    }

    @JsonProperty(ANONYMOUS_COUNT)
    public Optional<Long> getAnonymousCount() {
        return anonymousCount;
    }

    @JsonProperty(NON_PROCESSED_COUNT)
    public Optional<Long> getNonProcessedCount() {
        return nonProcessedCount;
    }
}
