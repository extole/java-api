package com.extole.consumer.rest.person.asset.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class PersonAssetResponse {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String FILENAME = "filename";
    private static final String MIME_TYPE = "mime_type";
    private static final String STATUS = "status";
    private static final String TAGS = "tags";
    private static final String DATA_TYPE = "data_type";

    private final String id;
    private final String name;
    private final String filename;
    private final String mimeType;
    private final PersonAssetStatus status;
    private final List<String> tags;
    private final DataType dataType;

    public PersonAssetResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(FILENAME) String filename,
        @JsonProperty(MIME_TYPE) String mimeType,
        @JsonProperty(STATUS) PersonAssetStatus status,
        @JsonProperty(TAGS) List<String> tags,
        @JsonProperty(DATA_TYPE) DataType dataType) {
        this.id = id;
        this.name = name;
        this.filename = filename;
        this.mimeType = mimeType;
        this.status = status;
        this.tags = tags;
        this.dataType = dataType;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(FILENAME)
    public String getFilename() {
        return filename;
    }

    @JsonProperty(MIME_TYPE)
    public String getMimeType() {
        return mimeType;
    }

    @JsonProperty(STATUS)
    public PersonAssetStatus getStatus() {
        return status;
    }

    @JsonProperty(TAGS)
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty(DATA_TYPE)
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id;
        private String name;
        private String filename;
        private String mimeType;
        private PersonAssetStatus status;
        private List<String> tags;
        private DataType dataType;

        private Builder() {
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder withStatus(PersonAssetStatus status) {
            this.status = status;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withPersonDataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public PersonAssetResponse build() {
            return new PersonAssetResponse(id, name, filename, mimeType, status, tags, dataType);
        }
    }
}
