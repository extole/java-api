package com.extole.client.rest.client;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.identity.IdentityKey;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ClientUpdateRequest {
    private static final String NAME = "name";
    private static final String CLIENT_TYPE = "client_type";
    private static final String POD = "pod";
    private static final String SLACK_CHANNEL_NAME = "slack_channel_name";
    private static final String IDENTITY_KEY = "identity_key";

    private final String name;
    private final ClientType clientType;
    private final String pod;
    private final Omissible<String> slackChannelName;
    private final Omissible<IdentityKey> identityKey;

    public ClientUpdateRequest(
        @Nullable @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(CLIENT_TYPE) ClientType clientType,
        @Nullable @JsonProperty(POD) String pod,
        @JsonProperty(SLACK_CHANNEL_NAME) Omissible<String> slackChannelName,
        @JsonProperty(IDENTITY_KEY) Omissible<IdentityKey> identityKey) {
        this.name = name;
        this.clientType = clientType;
        this.pod = pod;
        this.slackChannelName = slackChannelName;
        this.identityKey = identityKey;
    }

    @Nullable
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(CLIENT_TYPE)
    public ClientType getClientType() {
        return clientType;
    }

    @Nullable
    @JsonProperty(POD)
    public String getPod() {
        return this.pod;
    }

    @JsonProperty(SLACK_CHANNEL_NAME)
    public Omissible<String> getSlackChannelName() {
        return slackChannelName;
    }

    @JsonProperty(IDENTITY_KEY)
    public Omissible<IdentityKey> getIdentityKey() {
        return identityKey;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private ClientType clientType;
        private String pod;
        private Omissible<String> slackChannel = Omissible.omitted();
        private Omissible<IdentityKey> identityKey = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withClientType(ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public Builder withPod(String pod) {
            this.pod = pod;
            return this;
        }

        public Builder withSlackChannel(String slackChannel) {
            this.slackChannel = Omissible.of(slackChannel);
            return this;
        }

        public Builder withIdentityKey(IdentityKey identityKey) {
            this.identityKey = Omissible.of(identityKey);
            return this;
        }

        public ClientUpdateRequest build() {
            return new ClientUpdateRequest(name, clientType, pod, slackChannel, identityKey);
        }
    }
}
