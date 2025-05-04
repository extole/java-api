package com.extole.client.rest.client;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.identity.IdentityKey;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class ClientCreationRequest {
    @Deprecated // TODO remove CLIENT_NAME as it duplicates NAME ENG-12530
    private static final String CLIENT_NAME = "client_name";
    @Deprecated // TODO remove DOMAIN_NAME as it duplicates SHORT_NAME ENG-12530
    private static final String DOMAIN_NAME = "domain_name";
    private static final String NAME = "name";
    private static final String SHORT_NAME = "short_name";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_FIRST_NAME = "user_first_name";
    private static final String USER_LAST_NAME = "user_last_name";
    private static final String USER_PASSWORD = "user_password";
    private static final String VERIFICATION_CODE = "verification_code";
    private static final String CLIENT_TYPE = "client_type";
    private static final String POD = "pod";
    private static final String PROPERTIES = "properties";
    private static final String SLACK_CHANNEL_NAME = "slack_channel_name";
    private static final String IDENTITY_KEY = "identity_key";

    private final String name;
    private final String shortName;
    private final String userEmail;
    private final String userFirstName;
    private final String userLastName;
    private final String userPassword;
    private final String verificationCode;
    private final String pod;
    private final ClientType clientType;
    private final Map<String, String> properties;
    private final Omissible<String> slackChannelName;
    private final Omissible<IdentityKey> identityKey;

    public ClientCreationRequest(
        // TODO name must be required after removing client_name ENG-12530
        @Nullable @JsonProperty(NAME) String name,
        @Nullable @JsonProperty(SHORT_NAME) String shortName,
        @Deprecated // TODO remove client_name as it duplicates 'name' ENG-12530
        @Nullable @JsonProperty(CLIENT_NAME) String clientName,
        @Deprecated // TODO remove domain_name as it duplicates 'short_name' ENG-12530
        @Nullable @JsonProperty(DOMAIN_NAME) String domainName,
        @JsonProperty(USER_EMAIL) String userEmail,
        @JsonProperty(USER_PASSWORD) String userPassword,
        @Nullable @JsonProperty(USER_FIRST_NAME) String userFirstName,
        @Nullable @JsonProperty(USER_LAST_NAME) String userLastName,
        @Nullable @JsonProperty(VERIFICATION_CODE) String verificationCode,
        @Nullable @JsonProperty(POD) String pod,
        @Nullable @JsonProperty(CLIENT_TYPE) ClientType clientType,
        @Nullable @JsonProperty(PROPERTIES) Map<String, String> properties,
        @JsonProperty(SLACK_CHANNEL_NAME) Omissible<String> slackChannelName,
        @JsonProperty(IDENTITY_KEY) Omissible<IdentityKey> identityKey) {
        this.name = Objects.isNull(name) ? clientName : name;
        this.shortName = Objects.isNull(shortName) ? domainName : shortName;
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPassword = userPassword;
        this.verificationCode = verificationCode;
        this.pod = pod;
        this.clientType = clientType;
        this.properties = properties;
        this.slackChannelName = slackChannelName;
        this.identityKey = identityKey;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(SHORT_NAME)
    public String getShortName() {
        return shortName;
    }

    @Nullable
    @JsonProperty(USER_EMAIL)
    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    @JsonProperty(USER_FIRST_NAME)
    public String getUserFirstName() {
        return userFirstName;
    }

    @Nullable
    @JsonProperty(USER_LAST_NAME)
    public String getUserLastName() {
        return userLastName;
    }

    @Nullable
    @JsonProperty(USER_PASSWORD)
    public String getUserPassword() {
        return userPassword;
    }

    @Nullable
    @JsonProperty(VERIFICATION_CODE)
    public String getVerificationCode() {
        return verificationCode;
    }

    @Nullable
    @JsonProperty(POD)
    public String getPod() {
        return this.pod;
    }

    @Nullable
    @JsonProperty(CLIENT_TYPE)
    public ClientType getClientType() {
        return clientType;
    }

    @Nullable
    @JsonProperty(PROPERTIES)
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty(SLACK_CHANNEL_NAME)
    public Omissible<String> getSlackChannelName() {
        return slackChannelName;
    }

    @JsonProperty(IDENTITY_KEY)
    public Omissible<IdentityKey> getIdentityKey() {
        return identityKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private String name;
        private String shortName;
        private String userEmail;
        private String userFirstName;
        private String userLastName;
        private String userPassword;
        private String verificationCode;
        private String pod;
        private ClientType clientType;
        private Map<String, String> properties;
        private Omissible<String> slackChannel = Omissible.omitted();
        private Omissible<IdentityKey> identityKey = Omissible.omitted();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public Builder withUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder withUserFirstName(String userFirstName) {
            this.userFirstName = userFirstName;
            return this;
        }

        public Builder withUserLastName(String userLastName) {
            this.userLastName = userLastName;
            return this;
        }

        public Builder withUserPassword(String userPassword) {
            this.userPassword = userPassword;
            return this;
        }

        public Builder withVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
            return this;
        }

        public Builder withPod(String pod) {
            this.pod = pod;
            return this;
        }

        public Builder withClientType(ClientType clientType) {
            this.clientType = clientType;
            return this;
        }

        public Builder withProperties(Map<String, String> properties) {
            this.properties = properties;
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

        public ClientCreationRequest build() {
            return new ClientCreationRequest(name, shortName, name, shortName, userEmail, userPassword, userFirstName,
                userLastName, verificationCode, pod, clientType, properties, slackChannel, identityKey);
        }
    }
}
