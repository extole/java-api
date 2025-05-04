package com.extole.client.rest.sftp;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema(description = "Extole local sftp destination create request.")
public final class LocalSftpDestinationCreateRequest extends SftpDestinationCreateRequest {

    static final String TYPE = "LOCAL";
    private static final String JSON_KEY_IDS = "key_ids";
    private static final String JSON_FILE_PROCESSING_ENABLED = "file_processing_enabled";

    private final Omissible<String> username;
    private final Omissible<Boolean> fileProcessingEnabled;
    private final Set<String> keyIds;

    @JsonCreator
    LocalSftpDestinationCreateRequest(@JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_KEY_IDS) Set<String> keyIds,
        @JsonProperty(JSON_PARTNER_KEY_ID) Omissible<String> partnerKeyId,
        @JsonProperty(JSON_EXTOLE_KEY_ID) Omissible<String> extoleKeyId,
        @JsonProperty(JSON_USERNAME) Omissible<String> username,
        @JsonProperty(JSON_FILE_PROCESSING_ENABLED) Omissible<Boolean> fileProcessingEnabled) {
        super(SftpDestinationType.LOCAL, name, partnerKeyId, extoleKeyId);
        this.username = username;
        this.fileProcessingEnabled = fileProcessingEnabled;
        this.keyIds = keyIds == null ? Collections.emptySet() : Collections.unmodifiableSet(keyIds);
    }

    @JsonProperty(JSON_USERNAME)
    public Omissible<String> getUsername() {
        return username;
    }

    @JsonProperty(JSON_FILE_PROCESSING_ENABLED)
    public Omissible<Boolean> getFileProcessingEnabled() {
        return fileProcessingEnabled;
    }

    @JsonProperty(JSON_KEY_IDS)
    public Set<String> getKeyIds() {
        return keyIds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SftpDestinationCreateRequestBuilder<Builder> {

        private Omissible<String> username = Omissible.omitted();
        private Set<String> keyIds = Collections.emptySet();
        private Omissible<Boolean> fileProcessingEnabled = Omissible.omitted();

        private Builder() {
        }

        public Builder withUsername(Omissible<String> username) {
            this.username = username;
            return this;
        }

        public Builder withKeyIds(Set<String> keyIds) {
            this.keyIds = keyIds;
            return this;
        }

        public Builder withFileProcessingEnabled(Omissible<Boolean> fileProcessingEnabled) {
            this.fileProcessingEnabled = fileProcessingEnabled;
            return this;
        }

        public LocalSftpDestinationCreateRequest build() {
            return new LocalSftpDestinationCreateRequest(name, keyIds, partnerKeyId, extoleKeyId, username,
                fileProcessingEnabled);
        }
    }
}
