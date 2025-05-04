package com.extole.consumer.rest.shareable.v4;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated // TODO remove ENG-10127
public final class CreateShareableV4Response {
    private final String pollingId;
    private final String shareableId;
    private final String shareableCode;
    private final String shareableDomain;
    private final String shareableProgramId;

    public CreateShareableV4Response(@JsonProperty("polling_id") String pollingId,
        @JsonProperty("shareable_id") String shareableId, @JsonProperty("shareable_code") String shareableCode,
        @JsonProperty("shareable_domain") String shareableDomain,
        @JsonProperty("shareable_program_id") String shareableProgramId) {
        this.pollingId = pollingId;
        this.shareableId = shareableId;
        this.shareableCode = shareableCode;
        this.shareableDomain = shareableDomain;
        this.shareableProgramId = shareableProgramId;
    }

    @JsonProperty("polling_id")
    public String getPollingId() {
        return pollingId;
    }

    @Deprecated // TODO remove ENG-10127
    @JsonProperty("shareable_id")
    public String getShareableId() {
        return shareableId;
    }

    @Deprecated // TODO remove ENG-10127
    @JsonProperty("shareable_code")
    public String getShareableCode() {
        return shareableCode;
    }

    @Deprecated // TODO remove ENG-10127
    @JsonProperty("shareable_domain")
    public String getShareableDomain() {
        return shareableDomain;
    }

    @JsonProperty("shareable_program_id")
    public String getShareableProgramId() {
        return shareableProgramId;
    }
}
