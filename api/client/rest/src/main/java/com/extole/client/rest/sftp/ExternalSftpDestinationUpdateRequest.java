package com.extole.client.rest.sftp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

@Schema(description = "External sftp destination create request.")
public final class ExternalSftpDestinationUpdateRequest extends SftpDestinationUpdateRequest {

    static final String TYPE = "EXTERNAL";
    private static final String JSON_USERNAME = "username";
    private static final String JSON_DROPBOX_PATH = "dropbox_path";
    private static final String JSON_HOST = "host";
    private static final String JSON_PORT = "port";
    private static final String JSON_KEY_ID = "key_id";

    private final Omissible<String> username;
    private final Omissible<String> dropboxPath;
    private final Omissible<String> host;
    private final Omissible<Integer> port;
    private final Omissible<String> keyId;

    ExternalSftpDestinationUpdateRequest(@JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_KEY_ID) Omissible<String> keyId,
        @JsonProperty(JSON_PARTNER_KEY_ID) Omissible<String> partnerKeyId,
        @JsonProperty(JSON_EXTOLE_KEY_ID) Omissible<String> extoleKeyId,
        @JsonProperty(JSON_USERNAME) Omissible<String> username,
        @JsonProperty(JSON_DROPBOX_PATH) Omissible<String> dropboxPath,
        @JsonProperty(JSON_HOST) Omissible<String> host,
        @JsonProperty(JSON_PORT) Omissible<Integer> port) {
        super(SftpDestinationType.EXTERNAL, name, partnerKeyId, extoleKeyId);
        this.username = username;
        this.dropboxPath = dropboxPath;
        this.host = host;
        this.port = port;
        this.keyId = keyId;
    }

    @JsonProperty(JSON_USERNAME)
    public Omissible<String> getUsername() {
        return username;
    }

    @JsonProperty(JSON_DROPBOX_PATH)
    public Omissible<String> getDropboxPath() {
        return dropboxPath;
    }

    @JsonProperty(JSON_HOST)
    public Omissible<String> getHost() {
        return host;
    }

    @JsonProperty(JSON_PORT)
    public Omissible<Integer> getPort() {
        return port;
    }

    @JsonProperty(JSON_KEY_ID)
    public Omissible<String> getKeyId() {
        return keyId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends SftpDestinationUpdateRequestBuilder<Builder> {

        private Omissible<String> username = Omissible.omitted();
        private Omissible<String> dropboxPath = Omissible.omitted();
        private Omissible<String> host = Omissible.omitted();
        private Omissible<Integer> port = Omissible.omitted();
        private Omissible<String> keyId = Omissible.omitted();

        private Builder() {
        }

        public Builder withUsername(Omissible<String> username) {
            this.username = username;
            return this;
        }

        public Builder withDropboxPath(Omissible<String> dropboxPath) {
            this.dropboxPath = dropboxPath;
            return this;
        }

        public Builder withHost(Omissible<String> host) {
            this.host = host;
            return this;
        }

        public Builder withPort(Omissible<Integer> port) {
            this.port = port;
            return this;
        }

        public Builder withKeyId(Omissible<String> keyId) {
            this.keyId = keyId;
            return this;
        }

        public ExternalSftpDestinationUpdateRequest build() {
            return new ExternalSftpDestinationUpdateRequest(name, keyId, partnerKeyId, extoleKeyId, username,
                dropboxPath, host, port);
        }
    }
}
