package com.extole.client.rest.sftp;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Extole local sftp destination response.")
public final class LocalSftpDestinationResponse extends SftpDestinationResponse {

    static final String TYPE = "LOCAL";
    private static final String JSON_FILE_PROCESSING_ENABLED = "file_processing_enabled";
    private static final String JSON_KEY_IDS = "key_ids";

    private final boolean fileProcessingEnabled;
    private final Set<String> keyIds;

    @JsonCreator
    public LocalSftpDestinationResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_USERNAME) String username,
        @JsonProperty(JSON_DROPBOX_PATH) String dropboxPath,
        @JsonProperty(JSON_HOST) String host,
        @JsonProperty(JSON_PORT) int port,
        @JsonProperty(JSON_KEY_IDS) Set<String> keyIds,
        @Nullable @JsonProperty(JSON_PARTNER_KEY_ID) String partnerKeyId,
        @JsonProperty(JSON_EXTOLE_KEY_ID) String extoleKeyId,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(JSON_FILE_PROCESSING_ENABLED) boolean fileProcessingEnabled) {
        super(SftpDestinationType.LOCAL, id, name, username, dropboxPath, host, port, partnerKeyId, extoleKeyId,
            createdDate, updatedDate);
        this.fileProcessingEnabled = fileProcessingEnabled;
        this.keyIds = keyIds;
    }

    @JsonProperty(JSON_KEY_IDS)
    public Set<String> getKeyIds() {
        return keyIds;
    }

    @JsonProperty(JSON_FILE_PROCESSING_ENABLED)
    public boolean isFileProcessingEnabled() {
        return fileProcessingEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SftpDestinationResponseBuilder<Builder> {
        private Set<String> keyIds = Collections.emptySet();
        private boolean fileProcessingEnabled;

        private Builder() {
        }

        public Builder withKeyIds(Set<String> keyIds) {
            this.keyIds = keyIds;
            return this;
        }

        public Builder withFileProcessingEnabled(boolean fileProcessingEnabled) {
            this.fileProcessingEnabled = fileProcessingEnabled;
            return this;
        }

        public LocalSftpDestinationResponse build() {
            return new LocalSftpDestinationResponse(id, name, username, dropboxPath, host, port, keyIds, partnerKeyId,
                extoleKeyId, createdDate, updatedDate, fileProcessingEnabled);
        }
    }
}
