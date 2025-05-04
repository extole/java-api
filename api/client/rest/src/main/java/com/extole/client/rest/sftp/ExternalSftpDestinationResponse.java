package com.extole.client.rest.sftp;

import java.time.ZonedDateTime;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

@Schema(description = "External sftp destination response.")
public final class ExternalSftpDestinationResponse extends SftpDestinationResponse {

    static final String TYPE = "EXTERNAL";
    private static final String JSON_KEY_ID = "key_id";

    private final String keyId;

    @JsonCreator
    ExternalSftpDestinationResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_USERNAME) String username,
        @JsonProperty(JSON_DROPBOX_PATH) String dropboxPath,
        @JsonProperty(JSON_HOST) String host,
        @JsonProperty(JSON_PORT) int port,
        @JsonProperty(JSON_KEY_ID) String keyId,
        @Nullable @JsonProperty(JSON_PARTNER_KEY_ID) String partnerKeyId,
        @JsonProperty(JSON_EXTOLE_KEY_ID) String extoleKeyId,
        @JsonProperty(JSON_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_UPDATED_DATE) ZonedDateTime updatedDate) {
        super(SftpDestinationType.EXTERNAL, id, name, username, dropboxPath, host, port, partnerKeyId,
            extoleKeyId, createdDate, updatedDate);
        this.keyId = keyId;
    }

    @JsonProperty(JSON_KEY_ID)
    public String getKeyId() {
        return keyId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SftpDestinationResponseBuilder<Builder> {
        private String keyId;

        private Builder() {
        }

        public Builder withKeyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public ExternalSftpDestinationResponse build() {
            return new ExternalSftpDestinationResponse(id, name, username, dropboxPath, host, port, keyId, partnerKeyId,
                extoleKeyId, createdDate, updatedDate);
        }
    }
}
