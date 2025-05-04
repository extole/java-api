package com.extole.client.rest.person.iss;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = IdentityLogResponse.JSON_IDENTITY_LOG_TYPE,
    defaultImpl = Void.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PersonIdentityShapeShiftResponse.class, name = "IDENTITY_SHIFT"),
    @JsonSubTypes.Type(value = PersonIdentityHistoryResponse.class, name = "SPLIT_LOSER"),
})
public abstract class IdentityLogResponse {
    protected static final String JSON_IDENTITY_LOG_TYPE = "type";
    protected static final String JSON_OLD_IDENTITY_KEY_NAME = "old_identity_key_name";
    protected static final String JSON_OLD_IDENTITY_KEY_VALUE = "old_identity_key_value";
    protected static final String JSON_LOG_DATE = "log_date";

    private final IdentityLogType identityLogType;
    private final Optional<String> oldIdentityKeyName;
    private final Optional<String> oldIdentityKeyValue;
    private final Optional<ZonedDateTime> logDate;

    @JsonCreator
    protected IdentityLogResponse(
        @JsonProperty(JSON_IDENTITY_LOG_TYPE) IdentityLogType identityLogType,
        @JsonProperty(JSON_OLD_IDENTITY_KEY_NAME) Optional<String> oldIdentityKeyName,
        @JsonProperty(JSON_OLD_IDENTITY_KEY_VALUE) Optional<String> oldIdentityKeyValue,
        @JsonProperty(JSON_LOG_DATE) Optional<ZonedDateTime> logDate) {
        this.identityLogType = identityLogType;
        this.oldIdentityKeyName = oldIdentityKeyName;
        this.oldIdentityKeyValue = oldIdentityKeyValue;
        this.logDate = logDate;
    }

    @JsonProperty(JSON_IDENTITY_LOG_TYPE)
    public IdentityLogType getType() {
        return identityLogType;
    }

    @JsonProperty(JSON_OLD_IDENTITY_KEY_NAME)
    public Optional<String> getOldIdentityKeyName() {
        return oldIdentityKeyName;
    }

    @JsonProperty(JSON_OLD_IDENTITY_KEY_VALUE)
    public Optional<String> getOldIdentityKeyValue() {
        return oldIdentityKeyValue;
    }

    @JsonProperty(JSON_LOG_DATE)
    public Optional<ZonedDateTime> getLogDate() {
        return logDate;
    }

    public String toString() {
        return ToString.create(this);
    }

}
