package com.extole.reporting.rest.audience.list.request;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class UploadedAudienceListRequest extends AudienceListRequest {
    static final String AUDIENCE_TYPE = "UPLOADED";

    private static final String FILE_ASSET_ID = "file_asset_id";
    private static final String AUDIENCE_ID = "audience_id";

    private final String fileAssetId;
    private final Omissible<String> audienceId;

    public UploadedAudienceListRequest(
        @Parameter(description = "AudienceList name, max length 255")
        @JsonProperty(NAME) Omissible<String> name,
        @Parameter(description = "AudienceList description, max length 1024")
        @JsonProperty(DESCRIPTION) Omissible<String> description,
        @Parameter(description = "A list of columns that will be used when dispatching AudienceList")
        @JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @Parameter(description = "Data for the AudienceList")
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData,
        @Parameter(description = "A set of tags for the AudienceList")
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @Parameter(description = "An existing FileAsset id for the AudienceList")
        @JsonProperty(FILE_ASSET_ID) String fileAssetId,
        @Parameter(description = "An existing Audience id for the AudienceList")
        @JsonProperty(AUDIENCE_ID) Omissible<String> audienceId) {
        super(AudienceListType.UPLOADED, name, description, eventColumns, eventData, tags);
        this.fileAssetId = fileAssetId;
        this.audienceId = audienceId;
    }

    @JsonProperty(FILE_ASSET_ID)
    public String getFileAssetId() {
        return fileAssetId;
    }

    @JsonProperty(AUDIENCE_ID)
    public Omissible<String> getAudienceId() {
        return audienceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String fileAssetId;
        private Omissible<String> audienceId = Omissible.omitted();
        private Omissible<String> name = Omissible.omitted();
        private Omissible<String> description = Omissible.omitted();
        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withFileAssetId(String fileAssetId) {
            this.fileAssetId = fileAssetId;
            return this;
        }

        public Builder withAudienceId(String audienceId) {
            this.audienceId = Omissible.of(audienceId);
            return this;
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public UploadedAudienceListRequest build() {
            return new UploadedAudienceListRequest(name, description, eventColumns, eventData, tags, fileAssetId,
                audienceId);
        }
    }
}
