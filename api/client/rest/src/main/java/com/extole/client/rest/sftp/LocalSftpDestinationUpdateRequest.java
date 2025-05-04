package com.extole.client.rest.sftp;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema(description = "Extole local sftp destination update request.")
public final class LocalSftpDestinationUpdateRequest extends SftpDestinationUpdateRequest {

    static final String TYPE = "LOCAL";
    private static final String JSON_FILE_PROCESSING_ENABLED = "file_processing_enabled";
    private static final String JSON_KEY_IDS = "key_ids";

    private final Omissible<Boolean> fileProcessingEnabled;
    private final Omissible<Set<String>> keyIds;

    LocalSftpDestinationUpdateRequest(@JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_KEY_IDS) Omissible<Set<String>> keyIds,
        @JsonProperty(JSON_PARTNER_KEY_ID) Omissible<String> partnerKeyId,
        @JsonProperty(JSON_EXTOLE_KEY_ID) Omissible<String> extoleKeyId,
        @JsonProperty(JSON_FILE_PROCESSING_ENABLED) Omissible<Boolean> fileProcessingEnabled) {
        super(SftpDestinationType.LOCAL, name, partnerKeyId, extoleKeyId);
        this.fileProcessingEnabled = fileProcessingEnabled;
        this.keyIds = keyIds;
    }

    @JsonProperty(JSON_FILE_PROCESSING_ENABLED)
    public Omissible<Boolean> getFileProcessingEnabled() {
        return fileProcessingEnabled;
    }

    @JsonProperty(JSON_KEY_IDS)
    public Omissible<Set<String>> getKeyIds() {
        return keyIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SftpDestinationUpdateRequestBuilder<Builder> {

        private Omissible<Boolean> fileProcessingEnabled = Omissible.omitted();
        private Omissible<Set<String>> keyIds = Omissible.omitted();

        private Builder() {
        }

        public Builder withFileProcessingEnabled(Omissible<Boolean> fileProcessingEnabled) {
            this.fileProcessingEnabled = fileProcessingEnabled;
            return this;
        }

        public Builder withKeyIds(Omissible<Set<String>> keyIds) {
            this.keyIds = keyIds;
            return this;
        }

        public LocalSftpDestinationUpdateRequest build() {
            return new LocalSftpDestinationUpdateRequest(name, keyIds, partnerKeyId, extoleKeyId,
                fileProcessingEnabled);
        }
    }
}
