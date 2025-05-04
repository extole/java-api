
package com.extole.client.rest.campaign;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public final class CampaignMakeLatestRequest {

    private static final String MESSAGE = "message";
    private static final String VERSION = "version";

    private final Omissible<Optional<String>> message;
    private final Omissible<String> version;

    @JsonCreator
    private CampaignMakeLatestRequest(
        @JsonProperty(MESSAGE) Omissible<Optional<String>> message,
        @JsonProperty(VERSION) Omissible<String> version) {
        this.message = message;
        this.version = version;
    }

    @JsonProperty(MESSAGE)
    public Omissible<Optional<String>> getMessage() {
        return message;
    }

    @JsonProperty(VERSION)
    public Omissible<String> getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(CampaignMakeLatestRequest campaignMakeLatestRequest) {
        return new Builder(campaignMakeLatestRequest);
    }

    public static final class Builder {
        private Omissible<Optional<String>> message = Omissible.omitted();
        private Omissible<String> version = Omissible.omitted();

        private Builder() {
        }

        private Builder(CampaignMakeLatestRequest campaignMakeLatestRequest) {
            this.message = campaignMakeLatestRequest.getMessage();
            this.version = campaignMakeLatestRequest.getVersion();
        }

        public Builder withMessage(String message) {
            this.message = Omissible.of(Optional.of(message));
            return this;
        }

        public Builder withVersion(String version) {
            this.version = Omissible.of(version);
            return this;
        }

        public CampaignMakeLatestRequest build() {
            return new CampaignMakeLatestRequest(message, version);
        }
    }

}
