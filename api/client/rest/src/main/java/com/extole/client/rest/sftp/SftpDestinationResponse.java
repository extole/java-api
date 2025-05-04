package com.extole.client.rest.sftp;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = SftpDestinationResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ExternalSftpDestinationResponse.class, name = ExternalSftpDestinationResponse.TYPE),
    @JsonSubTypes.Type(value = LocalSftpDestinationResponse.class, name = LocalSftpDestinationResponse.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = ExternalSftpDestinationResponse.TYPE, schema = ExternalSftpDestinationResponse.class),
    @DiscriminatorMapping(value = LocalSftpDestinationResponse.TYPE, schema = LocalSftpDestinationResponse.class)
})
public abstract class SftpDestinationResponse {

    static final String JSON_TYPE = "type";
    static final String JSON_ID = "id";
    static final String JSON_NAME = "name";
    static final String JSON_USERNAME = "username";
    static final String JSON_PARTNER_KEY_ID = "partner_key_id";
    static final String JSON_EXTOLE_KEY_ID = "extole_key_id";
    static final String JSON_DROPBOX_PATH = "dropbox_path";
    static final String JSON_HOST = "host";
    static final String JSON_PORT = "port";
    static final String JSON_CREATED_DATE = "created_date";
    static final String JSON_UPDATED_DATE = "updated_date";

    private final SftpDestinationType type;
    private final String id;
    private final String name;
    private final String username;
    private final Optional<String> partnerKeyId;
    private final String extoleKeyId;
    private final String dropboxPath;
    private final String host;
    private final int port;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    SftpDestinationResponse(SftpDestinationType type,
        String id,
        String name,
        String username,
        String dropboxPath,
        String host,
        int port,
        @Nullable String partnerKeyId,
        String extoleKeyId,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.username = username;
        this.dropboxPath = dropboxPath;
        this.host = host;
        this.port = port;
        this.partnerKeyId = Optional.ofNullable(partnerKeyId);
        this.extoleKeyId = extoleKeyId;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_TYPE)
    public SftpDestinationType getType() {
        return type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_USERNAME)
    public String getUsername() {
        return username;
    }

    @JsonProperty(JSON_DROPBOX_PATH)
    public String getDropboxPath() {
        return dropboxPath;
    }

    @JsonProperty(JSON_HOST)
    public String getHost() {
        return host;
    }

    @JsonProperty(JSON_PORT)
    public int getPort() {
        return port;
    }

    @JsonProperty(JSON_PARTNER_KEY_ID)
    public Optional<String> getPartnerKeyId() {
        return partnerKeyId;
    }

    @JsonProperty(JSON_EXTOLE_KEY_ID)
    public String getExtoleKeyId() {
        return extoleKeyId;
    }

    @JsonProperty(JSON_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static class SftpDestinationResponseBuilder<T extends SftpDestinationResponseBuilder> {

        protected String id;
        protected String name;
        protected String username;
        protected String dropboxPath;
        protected String host;
        protected int port;
        protected String partnerKeyId;
        protected String extoleKeyId;
        protected ZonedDateTime createdDate;
        protected ZonedDateTime updatedDate;

        public T withId(String id) {
            this.id = id;
            return (T) this;
        }

        public T withName(String name) {
            this.name = name;
            return (T) this;
        }

        public T withUsername(String username) {
            this.username = username;
            return (T) this;
        }

        public T withDropboxPath(String dropboxPath) {
            this.dropboxPath = dropboxPath;
            return (T) this;
        }

        public T withHost(String host) {
            this.host = host;
            return (T) this;
        }

        public T withPort(int port) {
            this.port = port;
            return (T) this;
        }

        public T withPartnerKeyId(String partnerKeyId) {
            this.partnerKeyId = partnerKeyId;
            return (T) this;
        }

        public T withExtoleKeyId(String extoleKeyId) {
            this.extoleKeyId = extoleKeyId;
            return (T) this;
        }

        public T withCreatedDate(ZonedDateTime createdDate) {
            this.createdDate = createdDate;
            return (T) this;
        }

        public T withUpdatedDate(ZonedDateTime updatedDate) {
            this.updatedDate = updatedDate;
            return (T) this;
        }
    }
}
