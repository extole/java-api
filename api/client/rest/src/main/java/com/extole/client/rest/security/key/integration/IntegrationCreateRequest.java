package com.extole.client.rest.security.key.integration;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class IntegrationCreateRequest {
    private static final String JSON_CODE = "code";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_NAME = "name";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_PARTNER_KEY_ID = "partner_key_id";

    private final String code;
    private final Omissible<String> description;
    private final Omissible<String> name;
    private final Omissible<String> partnerKeyId;
    private final Omissible<Set<String>> tags;

    public IntegrationCreateRequest(@JsonProperty(JSON_CODE) String code,
        @JsonProperty(JSON_DESCRIPTION) Omissible<String> description,
        @JsonProperty(JSON_NAME) Omissible<String> name,
        @JsonProperty(JSON_PARTNER_KEY_ID) Omissible<String> partnerKeyId,
        @JsonProperty(JSON_TAGS) Omissible<Set<String>> tags) {
        this.code = code;
        this.description = description;
        this.name = name;
        this.partnerKeyId = partnerKeyId;
        this.tags = tags;
    }

    @JsonProperty(JSON_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_DESCRIPTION)
    public Omissible<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_PARTNER_KEY_ID)
    public Omissible<String> getPartnerKeyId() {
        return partnerKeyId;
    }

    @JsonProperty(JSON_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String code;
        private Omissible<String> description = Omissible.omitted();
        private Omissible<String> name = Omissible.omitted();
        private Omissible<String> partnerKeyId = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withDescription(Omissible<String> description) {
            this.description = description;
            return this;
        }

        public Builder withName(Omissible<String> name) {
            this.name = name;
            return this;
        }

        public Builder withTags(Omissible<Set<String>> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withPartnerKeyId(Omissible<String> partnerKeyId) {
            this.partnerKeyId = partnerKeyId;
            return this;
        }

        public IntegrationCreateRequest build() {
            return new IntegrationCreateRequest(code, description, name, partnerKeyId, tags);
        }
    }
}
