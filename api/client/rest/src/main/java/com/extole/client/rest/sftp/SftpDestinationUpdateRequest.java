package com.extole.client.rest.sftp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.rest.omissible.Omissible;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = SftpDestinationUpdateRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ExternalSftpDestinationUpdateRequest.class,
        name = ExternalSftpDestinationUpdateRequest.TYPE),
    @JsonSubTypes.Type(value = LocalSftpDestinationUpdateRequest.class, name = LocalSftpDestinationUpdateRequest.TYPE)
})
@Schema(discriminatorProperty = "type", discriminatorMapping = {
    @DiscriminatorMapping(value = ExternalSftpDestinationUpdateRequest.TYPE,
        schema = ExternalSftpDestinationUpdateRequest.class),
    @DiscriminatorMapping(value = LocalSftpDestinationUpdateRequest.TYPE,
        schema = LocalSftpDestinationUpdateRequest.class)
})
public abstract class SftpDestinationUpdateRequest {

    static final String JSON_TYPE = "type";
    static final String JSON_NAME = "name";
    static final String JSON_PARTNER_KEY_ID = "partner_key_id";
    static final String JSON_EXTOLE_KEY_ID = "extole_key_id";

    private final SftpDestinationType type;
    private final Omissible<String> name;
    private final Omissible<String> partnerKeyId;
    private final Omissible<String> extoleKeyId;

    public SftpDestinationUpdateRequest(SftpDestinationType type,
        Omissible<String> name,
        Omissible<String> partnerKeyId,
        Omissible<String> extoleKeyId) {
        this.type = type;
        this.name = name;
        this.partnerKeyId = partnerKeyId;
        this.extoleKeyId = extoleKeyId;

    }

    @JsonProperty(JSON_TYPE)
    public SftpDestinationType getType() {
        return type;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_PARTNER_KEY_ID)
    public Omissible<String> getPartnerKeyId() {
        return partnerKeyId;
    }

    @JsonProperty(JSON_EXTOLE_KEY_ID)
    public Omissible<String> getExtoleKeyId() {
        return extoleKeyId;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static class SftpDestinationUpdateRequestBuilder<T extends SftpDestinationUpdateRequestBuilder> {

        protected Omissible<String> name = Omissible.omitted();
        protected Omissible<String> partnerKeyId = Omissible.omitted();
        protected Omissible<String> extoleKeyId = Omissible.omitted();

        public T withName(Omissible<String> name) {
            this.name = name;
            return (T) this;
        }

        public T withPartnerKeyId(Omissible<String> partnerKeyId) {
            this.partnerKeyId = partnerKeyId;
            return (T) this;
        }

        public T withExtoleKeyId(Omissible<String> extoleKeyId) {
            this.extoleKeyId = extoleKeyId;
            return (T) this;
        }
    }
}
