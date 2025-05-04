package com.extole.client.rest.person.iss;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonIdentityHistoryResponse extends IdentityLogResponse {
    private static final String JSON_WINNING_IDENTITY_KEY_NAME = "winning_identity_key_name";
    private static final String JSON_WINNING_IDENTITY_KEY_VALUE = "winning_identity_key_value";
    private static final String JSON_WINNING_IDENTITY_ID = "winning_identity_id";
    private static final String JSON_LOSING_IDENTITY_KEY_VALUE = "losing_identity_key_value";

    private final Optional<String> winningIdentityKeyName;
    private final Optional<String> winningIdentityKeyValue;
    private final Optional<String> winningIdentityId;
    private final Optional<String> losingIdentityKeyValue;

    @JsonCreator
    public PersonIdentityHistoryResponse(
        @JsonProperty(JSON_OLD_IDENTITY_KEY_NAME) Optional<String> oldIdentityKeyName,
        @JsonProperty(JSON_OLD_IDENTITY_KEY_VALUE) Optional<String> oldIdentityKeyValue,
        @JsonProperty(JSON_LOG_DATE) Optional<ZonedDateTime> logDate,
        @JsonProperty(JSON_WINNING_IDENTITY_KEY_NAME) Optional<String> winningIdentityKeyName,
        @JsonProperty(JSON_WINNING_IDENTITY_KEY_VALUE) Optional<String> winningIdentityKeyValue,
        @JsonProperty(JSON_WINNING_IDENTITY_ID) Optional<String> winningIdentityId,
        @JsonProperty(JSON_LOSING_IDENTITY_KEY_VALUE) Optional<String> losingIdentityKeyValue) {
        super(IdentityLogType.SPLIT_LOSER, oldIdentityKeyName, oldIdentityKeyValue, logDate);
        this.winningIdentityKeyName = winningIdentityKeyName;
        this.winningIdentityKeyValue = winningIdentityKeyValue;
        this.winningIdentityId = winningIdentityId;
        this.losingIdentityKeyValue = losingIdentityKeyValue;
    }

    @JsonProperty(JSON_WINNING_IDENTITY_KEY_NAME)
    public Optional<String> getWinningIdentityKeyName() {
        return winningIdentityKeyName;
    }

    @JsonProperty(JSON_WINNING_IDENTITY_KEY_VALUE)
    public Optional<String> getWinningIdentityKeyValue() {
        return winningIdentityKeyValue;
    }

    @JsonProperty(JSON_WINNING_IDENTITY_ID)
    public Optional<String> getWinningIdentityId() {
        return winningIdentityId;
    }

    @JsonProperty(JSON_LOSING_IDENTITY_KEY_VALUE)
    public Optional<String> getLosingIdentityKeyValue() {
        return losingIdentityKeyValue;
    }
}
