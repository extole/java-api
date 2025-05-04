package com.extole.client.rest.sftp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class SftpDestinationValidationResponse {

    private static final String JSON_SFTP_DESTINATION = "sftp_destination";
    private static final String JSON_STATUS = "status";
    private static final String JSON_REASON = "reason";

    private final SftpDestinationResponse sftpDestination;
    private final SftpValidationStatus status;
    private final String statusReason;

    @JsonCreator
    SftpDestinationValidationResponse(
        @JsonProperty(JSON_SFTP_DESTINATION) SftpDestinationResponse sftpDestination,
        @JsonProperty(JSON_STATUS) SftpValidationStatus status,
        @JsonProperty(JSON_REASON) String statusReason) {
        this.sftpDestination = sftpDestination;
        this.status = status;
        this.statusReason = statusReason;
    }

    @JsonProperty(JSON_SFTP_DESTINATION)
    public SftpDestinationResponse getSftpDestination() {
        return sftpDestination;
    }

    @JsonProperty(JSON_STATUS)
    public SftpValidationStatus getStatus() {
        return status;
    }

    @JsonProperty(JSON_REASON)
    public String getStatusReason() {
        return statusReason;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private SftpDestinationResponse sftpDestination;
        private SftpValidationStatus status;
        private String statusReason;

        private Builder() {
        }

        public Builder withSftpDestination(SftpDestinationResponse sftpDestination) {
            this.sftpDestination = sftpDestination;
            return this;
        }

        public Builder withStatus(SftpValidationStatus status) {
            this.status = status;
            return this;
        }

        public Builder withStatusReason(String statusReason) {
            this.statusReason = statusReason;
            return this;
        }

        public SftpDestinationValidationResponse build() {
            return new SftpDestinationValidationResponse(sftpDestination, status, statusReason);
        }
    }
}
